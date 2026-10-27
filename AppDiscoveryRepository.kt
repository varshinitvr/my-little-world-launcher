package com.mylittleworld.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Process
import android.util.LruCache
import android.widget.Toast
import com.mylittleworld.launcher.model.AppCategory
import com.mylittleworld.launcher.model.InstalledApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Discovers real launchable applications installed on the Redmi 12 5G using Android's
 * official LauncherApps API, caches scaled icons in a bounded LruCache to keep memory low,
 * and launches apps via real Android system intents.
 */
class AppDiscoveryRepository(private val context: Context) {

    // Bounded icon cache (max ~8MB) so scrolling or switching rooms never re-allocates bitmaps
    private val iconCache = object : LruCache<String, Bitmap>(8 * 1024) {
        override fun sizeOf(key: String, value: Bitmap): Int = value.byteCount / 1024
    }

    /**
     * Queries all launchable activities across the current user profile.
     * Runs on Dispatchers.IO for fast, non-blocking startup on Redmi 12 5G.
     */
    suspend fun loadInstalledApps(): List<InstalledApp> = withContext(Dispatchers.IO) {
        val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
            ?: return@withContext emptyList()

        val userHandle = Process.myUserHandle()
        val activityList = try {
            launcherApps.getActivityList(null, userHandle)
        } catch (e: Exception) {
            emptyList()
        }

        val myPackageName = context.packageName

        activityList
            .asSequence()
            .filter { it.applicationInfo.packageName != myPackageName }
            .map { info ->
                val pkg = info.applicationInfo.packageName
                val actName = info.componentName.className
                val cacheKey = "$pkg/$actName"
                val bitmap = iconCache.get(cacheKey) ?: run {
                    val drawable = info.getBadgedIcon(0)
                    drawableToBitmap(drawable, targetSizePx = 112).also { bmp ->
                        iconCache.put(cacheKey, bmp)
                    }
                }
                val label = info.label?.toString()?.trim().takeUnless { it.isNullOrEmpty() } ?: pkg
                InstalledApp(
                    label = label,
                    packageName = pkg,
                    activityName = actName,
                    iconBitmap = bitmap,
                    categoryHint = classifyPackage(pkg, label)
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
            .toList()
    }

    /**
     * Launches an installed application using its real Android launch intent.
     * Never fakes or simulates launches.
     */
    fun launchApp(app: InstalledApp): Boolean {
        return try {
            val launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as? LauncherApps
            val component = ComponentName(app.packageName, app.activityName)
            if (launcherApps != null) {
                launcherApps.startMainActivity(component, Process.myUserHandle(), null, null)
                true
            } else {
                val launchIntent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    true
                } else {
                    Toast.makeText(context, "Unable to launch ${app.label}", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open ${app.label}: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    /**
     * Heuristic classification into our 6 house room categories based on real Indian & global package names.
     */
    private fun classifyPackage(packageName: String, label: String): AppCategory {
        val pkg = packageName.lowercase()
        val name = label.lowercase()

        return when {
            pkg.contains("openai") || pkg.contains("claude") || pkg.contains("bard") ||
                pkg.contains("gemini") || pkg.contains("perplexity") || pkg.contains("copilot") ||
                name.contains("chatgpt") || name.contains("claude") || name.contains("gemini") ->
                AppCategory.AI_AND_RESEARCH

            pkg.contains("docs") || pkg.contains("drive") || pkg.contains("chrome") ||
                pkg.contains("teams") || pkg.contains("fileexplorer") || pkg.contains("documentsui") ||
                pkg.contains("scanner") || pkg.contains("camscanner") || pkg.contains("office") ||
                pkg.contains("browser") || name.contains("downloads") || name.contains("files") ->
                AppCategory.WORK_AND_FILES

            pkg.contains("photoshop") || pkg.contains("lightroom") || pkg.contains("canva") ||
                pkg.contains("capcut") || pkg.contains("lvoverseas") || pkg.contains("inshot") ||
                pkg.contains("picsart") || pkg.contains("snapseed") || pkg.contains("gallery") ||
                pkg.contains("camera") || name.contains("editor") ->
                AppCategory.CREATIVE_TOOLS

            pkg.contains("calendar") || pkg.contains("notes") || pkg.contains("keep") ||
                pkg.contains("notion") || pkg.contains("obsidian") || pkg.contains("todo") ||
                pkg.contains("ticktick") || pkg.contains("tasks") ->
                AppCategory.NOTES_AND_PLANNING

            pkg.contains("youtube") || pkg.contains("netflix") || pkg.contains("hotstar") ||
                pkg.contains("jiocinema") || pkg.contains("jio.media") || pkg.contains("sonyliv") ||
                pkg.contains("vlc") || pkg.contains("letterboxd") || pkg.contains("spotify") ||
                pkg.contains("videos") || pkg.contains("primevideo") || pkg.contains("sunnxt") ||
                name.contains("tv") || name.contains("music") ->
                AppCategory.ENTERTAINMENT_TV

            pkg.contains("swiggy") || pkg.contains("zomato") || pkg.contains("blinkit") ||
                pkg.contains("grofers") || pkg.contains("zepto") || pkg.contains("bigbasket") ||
                pkg.contains("amazon") || pkg.contains("flipkart") || pkg.contains("myntra") ||
                name.contains("swiggy") || name.contains("blinkit") || name.contains("zepto") ->
                AppCategory.FOOD_AND_SHOPPING

            pkg.contains("noisefit") || pkg.contains("cure.fit") || pkg.contains("cult") ||
                pkg.contains("health") || pkg.contains("weather") || pkg.contains("deskclock") ||
                pkg.contains("clock") || pkg.contains("meditation") || pkg.contains("headspace") ||
                pkg.contains("fitness") || name.contains("wellness") || name.contains("clock") ->
                AppCategory.WELLNESS_AND_HEALTH

            pkg.contains("whatsapp") || pkg.contains("android.gm") || pkg.contains("messaging") ||
                pkg.contains("mms") || pkg.contains("dialer") || pkg.contains("contacts") ||
                pkg.contains("instagram") || pkg.contains("maps") || pkg.contains("olacabs") ||
                pkg.contains("rapido") || pkg.contains("uber") || pkg.contains("telegram") ||
                pkg.contains("digiyatra") || name.contains("phone") || name.contains("messages") ->
                AppCategory.COMMUNICATION_AND_TRAVEL

            else -> AppCategory.OTHER
        }
    }

    private fun drawableToBitmap(drawable: Drawable, targetSizePx: Int): Bitmap {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            return Bitmap.createScaledBitmap(drawable.bitmap, targetSizePx, targetSizePx, true)
        }
        val bitmap = Bitmap.createBitmap(targetSizePx, targetSizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}
