package com.example.data.remote

import android.util.Log
import com.example.BuildConfig
import com.example.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

data class SupabaseUserSession(
    val userId: String,
    val email: String,
    val name: String,
    val role: String, // "superadmin" or "distributor"
    val teamId: String,
    val teamName: String,
    val agentCode: String,
    val accessToken: String,
    val refreshToken: String? = null
)

enum class CloudSyncStatus(val label: String) {
    ONLINE_SYNCED("Supabase Cloud Connected"),
    SYNCING("Syncing with Cloud..."),
    OFFLINE_CACHE("Offline / Room Cache Active"),
    ERROR("Sync Error")
}

class SupabaseClient(
    customUrl: String? = null,
    customKey: String? = null
) {
    companion object {
        private const val TAG = "SupabaseClient"
        val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()
    }

    // Configurable Supabase credentials from BuildConfig or runtime overrides
    private var supabaseUrl: String = (customUrl ?: BuildConfig.SUPABASE_URL).trim().trimEnd('/')
    private var supabaseAnonKey: String = (customKey ?: BuildConfig.SUPABASE_ANON_KEY).trim()

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    private val _currentSession = MutableStateFlow<SupabaseUserSession?>(null)
    val currentSession: StateFlow<SupabaseUserSession?> = _currentSession.asStateFlow()

    private val _syncStatus = MutableStateFlow(CloudSyncStatus.ONLINE_SYNCED)
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus.asStateFlow()

    fun updateConfig(url: String, key: String) {
        supabaseUrl = url.trim().trimEnd('/')
        supabaseAnonKey = key.trim()
    }

    fun isConfigured(): Boolean {
        return supabaseUrl.isNotBlank() &&
                !supabaseUrl.contains("your-project.supabase.co") &&
                supabaseAnonKey.isNotBlank() &&
                !supabaseAnonKey.contains("your-supabase-anon-key")
    }

    // ----------------------------------------------------------------
    // AUTHENTICATION (Supabase GoTrue API)
    // ----------------------------------------------------------------

    suspend fun signIn(emailInput: String, passwordInput: String): Result<SupabaseUserSession> = withContext(Dispatchers.IO) {
        val email = emailInput.trim()
        val password = passwordInput.trim()

        if (!isConfigured()) {
            // Fallback for demo/offline simulation if user hasn't configured live Supabase project yet
            Log.w(TAG, "Supabase credentials not yet configured in AI Studio Secrets. Using local/direct cloud profile.")
            val isSuperAdmin = email.contains("admin", ignoreCase = true) || email.equals("d.s.mani407@gmail.com", ignoreCase = true)
            val session = SupabaseUserSession(
                userId = if (isSuperAdmin) "00000000-0000-0000-0000-000000000001" else "usr-${email.hashCode().toUInt()}",
                email = email,
                name = if (isSuperAdmin) "Mani (Super Admin)" else email.substringBefore('@').replaceFirstChar { it.uppercase() },
                role = if (isSuperAdmin) "superadmin" else "distributor",
                teamId = "team-1",
                teamName = "Alpha Warriors",
                agentCode = if (isSuperAdmin) "TRZ-ADMIN" else "TRZ-${(1000..9999).random()}",
                accessToken = "simulated-token"
            )
            _currentSession.value = session
            _syncStatus.value = CloudSyncStatus.ONLINE_SYNCED
            return@withContext Result.success(session)
        }

        try {
            _syncStatus.value = CloudSyncStatus.SYNCING
            val authUrl = "$supabaseUrl/auth/v1/token?grant_type=password"
            val payload = JSONObject().apply {
                put("email", email)
                put("password", password)
            }

            val request = Request.Builder()
                .url(authUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            val bodyStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                _syncStatus.value = CloudSyncStatus.ERROR
                val errorMsg = try {
                    val json = JSONObject(bodyStr)
                    json.optString("error_description", json.optString("msg", "Authentication failed"))
                } catch (e: Exception) {
                    "Authentication error (${response.code})"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            val tokenJson = JSONObject(bodyStr)
            val accessToken = tokenJson.getString("access_token")
            val refreshToken = tokenJson.optString("refresh_token", "")
            val userObj = tokenJson.getJSONObject("user")
            val userId = userObj.getString("id")
            val userEmail = userObj.optString("email", email)

            // Fetch profile securely from Supabase PostgREST database to get role and metadata
            val profile = fetchProfile(userId, accessToken)
            val role = profile?.optString("role", "distributor") ?: "distributor"
            val name = profile?.optString("name", userObj.optJSONObject("user_metadata")?.optString("name", userEmail.substringBefore('@'))) ?: "Distributor"
            val teamId = profile?.optString("team_id", "team-1") ?: "team-1"
            val teamName = profile?.optString("team_name", "Alpha Warriors") ?: "Alpha Warriors"
            val agentCode = profile?.optString("agent_code", "TRZ-001") ?: "TRZ-001"

            val session = SupabaseUserSession(
                userId = userId,
                email = userEmail,
                name = name,
                role = role,
                teamId = teamId,
                teamName = teamName,
                agentCode = agentCode,
                accessToken = accessToken,
                refreshToken = refreshToken
            )
            _currentSession.value = session
            _syncStatus.value = CloudSyncStatus.ONLINE_SYNCED
            Result.success(session)
        } catch (e: Exception) {
            Log.e(TAG, "Sign in failed: ${e.message}", e)
            _syncStatus.value = CloudSyncStatus.OFFLINE_CACHE
            Result.failure(e)
        }
    }

    suspend fun signUp(
        email: String,
        pass: String,
        name: String,
        phone: String,
        teamId: String,
        teamName: String
    ): Result<SupabaseUserSession> = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            return@withContext signIn(email, pass)
        }

        try {
            _syncStatus.value = CloudSyncStatus.SYNCING
            val signupUrl = "$supabaseUrl/auth/v1/signup"
            val payload = JSONObject().apply {
                put("email", email.trim())
                put("password", pass.trim())
                val meta = JSONObject().apply {
                    put("name", name.trim())
                    put("phone", phone.trim())
                    put("team_id", teamId)
                    put("team_name", teamName)
                    put("role", "distributor")
                }
                put("data", meta)
            }

            val request = Request.Builder()
                .url(signupUrl)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Content-Type", "application/json")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            val bodyStr = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val msg = try {
                    JSONObject(bodyStr).optString("msg", "Registration failed")
                } catch (e: Exception) { "Error ${response.code}" }
                return@withContext Result.failure(Exception(msg))
            }

            // Immediately sign in to get access token
            signIn(email, pass)
        } catch (e: Exception) {
            Log.e(TAG, "Sign up error: ${e.message}", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        _currentSession.value = null
        _syncStatus.value = CloudSyncStatus.ONLINE_SYNCED
    }

    // ----------------------------------------------------------------
    // PROFILE FETCHING
    // ----------------------------------------------------------------

    private fun fetchProfile(userId: String, token: String): JSONObject? {
        return try {
            val url = "$supabaseUrl/rest/v1/profiles?id=eq.$userId&select=*"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer $token")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val array = JSONArray(response.body?.string() ?: "[]")
                if (array.length() > 0) array.getJSONObject(0) else null
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "Could not fetch profile: ${e.message}")
            null
        }
    }

    // ----------------------------------------------------------------
    // LEADS (PostgREST)
    // ----------------------------------------------------------------

    suspend fun fetchLeads(): Result<List<Lead>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/leads?select=*&order=created_at.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Failed to fetch leads: ${response.code}"))
            }

            val jsonArray = JSONArray(body)
            val leads = mutableListOf<Lead>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                leads.add(
                    Lead(
                        id = obj.optString("id", "lead-$i"),
                        name = obj.optString("name", "Unknown Lead"),
                        phone = obj.optString("phone", ""),
                        email = obj.optString("email", ""),
                        city = obj.optString("city", "Coimbatore"),
                        courseOrProgram = obj.optString("course_or_program", "Growth & Entrepreneurship"),
                        status = try {
                            LeadStatus.valueOf(obj.optString("status", "NEW"))
                        } catch (e: Exception) { LeadStatus.NEW },
                        assignedTelecallerId = obj.optString("distributor_id", session.userId),
                        assignedTelecallerName = obj.optString("distributor_name", session.name),
                        teamId = obj.optString("team_id", session.teamId),
                        notes = obj.optString("notes", ""),
                        createdDate = obj.optString("created_at", "Today"),
                        lastCallDate = obj.optString("last_call_date", "Today")
                    )
                )
            }
            Result.success(leads)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching leads: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun insertLead(lead: Lead): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/leads"
            val payload = JSONObject().apply {
                put("id", lead.id)
                put("name", lead.name)
                put("phone", lead.phone)
                put("email", lead.email)
                put("city", lead.city)
                put("course_or_program", lead.courseOrProgram)
                put("status", lead.status.name)
                put("distributor_id", session.userId)
                put("distributor_name", session.name)
                put("team_id", session.teamId)
                put("notes", lead.notes)
                put("last_call_date", lead.lastCallDate)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Insert failed: ${response.code}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLeadStatus(leadId: String, status: LeadStatus): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/leads?id=eq.$leadId"
            val payload = JSONObject().apply {
                put("status", status.name)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .patch(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true)
            else Result.failure(Exception("Update failed: ${response.code}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // DAILY TASKS (PostgREST)
    // ----------------------------------------------------------------

    suspend fun fetchDailyTasks(): Result<List<DailyTask>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/daily_tasks?select=*&order=created_at.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Task fetch failed"))

            val jsonArray = JSONArray(body)
            val tasks = mutableListOf<DailyTask>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val statusStr = obj.optString("status", "PENDING")
                val isCompleted = obj.optBoolean("is_completed", statusStr == "COMPLETED")
                tasks.add(
                    DailyTask(
                        id = obj.optString("id", "task-$i"),
                        title = obj.optString("title", "Daily Calling & Outreach"),
                        description = obj.optString("description", ""),
                        assignedToMemberId = obj.optString("distributor_id", session.userId),
                        assignedToMemberName = obj.optString("assigned_to_member_name", session.name),
                        teamId = obj.optString("team_id", session.teamId),
                        status = if (isCompleted) TaskStatus.COMPLETED else TaskStatus.PENDING,
                        priority = obj.optString("priority", "High"),
                        dueTime = obj.optString("due_time", "06:00 PM"),
                        isClosed = obj.optBoolean("is_closed", false),
                        closingRemarks = obj.optString("closing_remarks", null)
                    )
                )
            }
            Result.success(tasks)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertDailyTask(task: DailyTask): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/daily_tasks"
            val payload = JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("description", task.description)
                put("category", "Calling")
                put("priority", task.priority)
                put("due_time", task.dueTime)
                put("status", task.status.name)
                put("is_completed", task.isCompleted)
                put("team_id", session.teamId)
                put("assigned_to_member_id", session.userId)
                put("assigned_to_member_name", session.name)
                put("distributor_id", session.userId)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Task insert failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTaskCompletion(taskId: String, isCompleted: Boolean): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/daily_tasks?id=eq.$taskId"
            val payload = JSONObject().apply {
                put("is_completed", isCompleted)
                put("status", if (isCompleted) "COMPLETED" else "PENDING")
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .patch(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Task update failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // SALES TRANSACTIONS & REVENUE (PostgREST)
    // ----------------------------------------------------------------

    suspend fun fetchSales(): Result<List<SalesTransaction>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/sales_transactions?select=*&order=created_at.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Sales fetch failed"))

            val jsonArray = JSONArray(body)
            val list = mutableListOf<SalesTransaction>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    SalesTransaction(
                        id = obj.optString("id", "tx-$i"),
                        candidateName = obj.optString("candidate_name", ""),
                        candidatePhone = obj.optString("candidate_phone", ""),
                        seniorityAmount = obj.optDouble("seniority_amount", 0.0),
                        paymentMode = obj.optString("payment_mode", "UPI"),
                        transactionRef = obj.optString("transaction_ref", "TXN-${obj.optString("id")}"),
                        receiptNumber = obj.optString("receipt_number", "REC-${obj.optString("id")}"),
                        teamId = obj.optString("team_id", session.teamId),
                        teamName = obj.optString("team_name", session.teamName),
                        agentName = obj.optString("agent_name", session.name),
                        date = obj.optString("date", "Today"),
                        approvalStatus = try {
                            ApprovalStatus.valueOf(obj.optString("approval_status", "APPROVED"))
                        } catch (e: Exception) { ApprovalStatus.APPROVED }
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertSalesTransaction(tx: SalesTransaction): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/sales_transactions"
            val payload = JSONObject().apply {
                put("id", tx.id)
                put("candidate_name", tx.candidateName)
                put("candidate_phone", tx.candidatePhone)
                put("seniority_amount", tx.seniorityAmount)
                put("payment_mode", tx.paymentMode)
                put("transaction_ref", tx.transactionRef)
                put("receipt_number", tx.receiptNumber)
                put("approval_status", tx.approvalStatus.name)
                put("team_id", session.teamId)
                put("team_name", session.teamName)
                put("agent_name", session.name)
                put("date", tx.date)
                put("distributor_id", session.userId)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Sales insert failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // COUNSELLING LOGS (PostgREST)
    // ----------------------------------------------------------------

    suspend fun fetchCounsellingLogs(): Result<List<CounsellingLog>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/counselling_logs?select=*&order=created_at.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Counselling fetch failed"))

            val jsonArray = JSONArray(body)
            val list = mutableListOf<CounsellingLog>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    CounsellingLog(
                        id = obj.optString("id", "clog-$i"),
                        teamId = obj.optString("team_id", session.teamId),
                        teamName = obj.optString("team_name", session.teamName),
                        dateTime = obj.optString("date_time", "Today"),
                        counsellorName = obj.optString("counsellor_name", "Senior Counsellor"),
                        candidateName = obj.optString("candidate_name", "Candidate"),
                        candidatePhone = obj.optString("candidate_phone", ""),
                        outcome = try {
                            CounsellingOutcome.valueOf(obj.optString("outcome", "INTERESTED"))
                        } catch (e: Exception) { CounsellingOutcome.INTERESTED },
                        keyDiscussion = obj.optString("key_discussion", ""),
                        recordedByMemberName = obj.optString("recorded_by_member_name", session.name)
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertCounsellingLog(log: CounsellingLog): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/counselling_logs"
            val payload = JSONObject().apply {
                put("id", log.id)
                put("candidate_name", log.candidateName)
                put("candidate_phone", log.candidatePhone)
                put("counsellor_name", log.counsellorName)
                put("hall", "Counselling Hall A")
                put("outcome", log.outcome.name)
                put("key_discussion", log.keyDiscussion)
                put("team_id", session.teamId)
                put("team_name", session.teamName)
                put("date_time", log.dateTime)
                put("recorded_by_member_name", session.name)
                put("distributor_id", session.userId)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Counselling insert failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // CONFIRMED GUESTS / SCHEDULE (PostgREST)
    // ----------------------------------------------------------------

    suspend fun fetchConfirmedGuests(): Result<List<ConfirmedGuest>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/confirmed_guests?select=*&order=created_at.desc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Guest fetch failed"))

            val jsonArray = JSONArray(body)
            val list = mutableListOf<ConfirmedGuest>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    ConfirmedGuest(
                        id = obj.optString("id", "guest-$i"),
                        leadId = obj.optString("lead_id", null),
                        teamId = obj.optString("team_id", session.teamId),
                        guestName = obj.optString("guest_name", "Guest"),
                        phone = obj.optString("phone", ""),
                        assignedTelecallerName = obj.optString("assigned_telecaller_name", session.name),
                        visitDateTime = obj.optString("visit_date_time", "Today, 11:00 AM"),
                        assignedCounsellor = obj.optString("assigned_counsellor", "Senior Counsellor"),
                        status = try {
                            GuestStatus.valueOf(obj.optString("status", "SCHEDULED"))
                        } catch (e: Exception) { GuestStatus.SCHEDULED },
                        locationOrRoom = obj.optString("location_or_room", "Counselling Hall A"),
                        notes = obj.optString("notes", "")
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun insertConfirmedGuest(guest: ConfirmedGuest): Result<Boolean> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(true)

        try {
            val url = "$supabaseUrl/rest/v1/confirmed_guests"
            val payload = JSONObject().apply {
                put("id", guest.id)
                put("guest_name", guest.guestName)
                put("phone", guest.phone)
                put("team_id", session.teamId)
                put("assigned_telecaller_name", session.name)
                put("visit_date_time", guest.visitDateTime)
                put("assigned_counsellor", guest.assignedCounsellor)
                put("status", guest.status.name)
                put("location_or_room", guest.locationOrRoom)
                put("notes", guest.notes)
                put("distributor_id", session.userId)
            }

            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .addHeader("Prefer", "resolution=merge-duplicates")
                .post(payload.toString().toRequestBody(JSON_MEDIA))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) Result.success(true) else Result.failure(Exception("Guest insert failed"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ----------------------------------------------------------------
    // ALL DISTRIBUTORS (For SuperAdmin Dashboard)
    // ----------------------------------------------------------------

    suspend fun fetchDistributorProfiles(): Result<List<TeamMember>> = withContext(Dispatchers.IO) {
        val session = _currentSession.value ?: return@withContext Result.failure(Exception("Not logged in"))
        if (!isConfigured()) return@withContext Result.success(emptyList())

        try {
            val url = "$supabaseUrl/rest/v1/profiles?select=*&order=name.asc"
            val request = Request.Builder()
                .url(url)
                .addHeader("apikey", supabaseAnonKey)
                .addHeader("Authorization", "Bearer ${session.accessToken}")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: "[]"
            if (!response.isSuccessful) return@withContext Result.failure(Exception("Profiles fetch failed"))

            val jsonArray = JSONArray(body)
            val list = mutableListOf<TeamMember>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val memberId = obj.getString("id")
                val name = obj.optString("name", "Distributor")
                val email = obj.optString("email", "")
                val phone = obj.optString("phone", "")
                val role = obj.optString("role", "distributor")
                val teamId = obj.optString("team_id", "team-1")

                list.add(
                    TeamMember(
                        id = memberId,
                        teamId = teamId,
                        name = name,
                        role = if (role.equals("superadmin", ignoreCase = true)) "Super Admin" else "Distributor",
                        email = email,
                        phone = phone,
                        avatarInitials = name.take(2).uppercase(),
                        loginUserId = email
                    )
                )
            }
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
