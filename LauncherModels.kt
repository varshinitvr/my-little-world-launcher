package com.mylittleworld.launcher.model

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color

/**
 * The six connected rooms in the 3-story house.
 */
enum class RoomId(
    val title: String,
    val subtitle: String,
    val floorLevel: Int, // 2 = Upper Floor, 1 = Middle Floor, 0 = Ground Floor
    val columnIndex: Int, // 0 = Left Wing, 1 = Right Wing
    val defaultGirlPose: GirlPoseState
) {
    BEDROOM(
        title = "Bedroom",
        subtitle = "Main Home Screen & Cozy Sanctuary",
        floorLevel = 2,
        columnIndex = 0,
        defaultGirlPose = GirlPoseState.LAPTOP_ON_BED
    ),
    WORKROOM(
        title = "Workroom",
        subtitle = "Workbench, Dual Monitors & Sorted Apps",
        floorLevel = 2,
        columnIndex = 1,
        defaultGirlPose = GirlPoseState.WORK_AT_DESK
    ),
    ENTERTAINMENT(
        title = "Entertainment",
        subtitle = "Real Television, Channels & Remote",
        floorLevel = 1,
        columnIndex = 0,
        defaultGirlPose = GirlPoseState.WATCH_TV_COUCH
    ),
    KITCHEN(
        title = "Kitchen",
        subtitle = "Opening App Cabinet & Quick Delivery",
        floorLevel = 1,
        columnIndex = 1,
        defaultGirlPose = GirlPoseState.COOKING_STOVE
    ),
    BATHROOM(
        title = "Bathroom",
        subtitle = "Mirror, Sink & Wellness Cabinet",
        floorLevel = 0,
        columnIndex = 0,
        defaultGirlPose = GirlPoseState.GETTING_READY_MIRROR
    ),
    ENTRANCE(
        title = "Entrance",
        subtitle = "Hallway, Mailbox & Daily Essentials",
        floorLevel = 0,
        columnIndex = 1,
        defaultGirlPose = GirlPoseState.HALLWAY_BACKPACK
    );

    companion object {
        fun fromName(name: String?): RoomId =
            entries.find { it.name.equals(name, ignoreCase = true) } ?: BEDROOM
    }
}

/**
 * Complete character poses for the protagonist girl across all 6 rooms
 * (matching the top-right "Character & Pet Animations" reference sheet).
 */
enum class GirlPoseState(
    val label: String,
    val speechBubble: String
) {
    WALKING("Walking", "Heading over right now!"),
    LAPTOP_ON_BED("Using Laptop on Bed", "A better me lives here. ♥"),
    RELAXING_ON_BED("Relaxing on Bed", "Just five more minutes of cozy peace..."),
    WORK_AT_DESK("Working at Dual Monitors", "Ideas, shoots, edit, repeat!"),
    WATCH_TV_COUCH("Watching TV with Popcorn", "Pass the popcorn! What channel is on?"),
    COOKING_STOVE("Cooking & Checking Cabinet", "Should I cook or order Swiggy/Blinkit?"),
    GETTING_READY_MIRROR("Getting Ready at Mirror", "Skincare, hydration, and good vibes!"),
    HALLWAY_BACKPACK("Ready at Hallway Mailbox", "Keys, backpack, messages, and ride booked!")
}

/**
 * Complete Tuxedo Cat animation states (Walk, Stretch, Sleep, Play, Follow).
 */
enum class CatPoseState(
    val label: String,
    val reactionText: String
) {
    WALK("Walk", "*Pitter-patter paws across the floor*"),
    STRETCH("Stretch", "*Big arched back stretch & yawn*"),
    SLEEP("Sleep", "Zzz... curled into a purring loaf"),
    PLAY("Play", "*Swats playfully at a glowing portal spark!*"),
    FOLLOW("Follow", "Meow! Following you from room to room")
}

/**
 * Complete Golden Dog animation states (Walk, Sit, Play, Sleep, Follow).
 */
enum class DogPoseState(
    val label: String,
    val reactionText: String
) {
    WALK("Walk", "*Happy trot with wagging tail*"),
    SIT("Sit", "*Sits politely waiting for treats*"),
    PLAY("Play", "Woof! Play-bow and zoomies!"),
    SLEEP("Sleep", "*Soft puppy snores on the rug*"),
    FOLLOW("Follow", "*Loyal companion following your footsteps*")
}

/**
 * Workroom Workbench sub-categories (AI & Research, Work & Files, Creative Tools, Notes & Planning).
 */
enum class WorkroomSubCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val accentColor: Color,
    val packageHints: List<String>
) {
    AI_AND_RESEARCH(
        id = "WORK_AI",
        title = "AI & Research",
        subtitle = "ChatGPT, Claude, Gemini & Assistants",
        accentColor = Color(0xFF4DD0E1),
        packageHints = listOf(
            "com.openai.chatgpt", "com.anthropic.claude", "com.google.android.apps.bard",
            "com.perplexity.app.android", "com.microsoft.copilot", "ai.character.app"
        )
    ),
    WORK_AND_FILES(
        id = "WORK_FILES",
        title = "Work & Files",
        subtitle = "Drive, Chrome, Teams, File Manager, Downloads, Scanner",
        accentColor = Color(0xFFFFB74D),
        packageHints = listOf(
            "com.google.android.apps.docs", "com.android.chrome", "com.microsoft.teams",
            "com.mi.android.globalFileexplorer", "com.android.documentsui", "com.intsig.camscanner",
            "com.adobe.scan.android", "com.microsoft.office.officehubrow", "com.slack", "us.zoom.videomeetings"
        )
    ),
    CREATIVE_TOOLS(
        id = "WORK_CREATIVE",
        title = "Editing & Creative Tools",
        subtitle = "Design, Image Creation & Video Editing",
        accentColor = Color(0xFFBA68C8),
        packageHints = listOf(
            "com.canva.editor", "com.adobe.psmobile", "com.adobe.lrmobile", "com.lemon.lvoverseas",
            "com.camerasideas.instashot", "com.picsart.studio", "com.niksoftware.snapseed",
            "com.miui.gallery", "com.android.camera", "com.figma.mirror"
        )
    ),
    NOTES_AND_PLANNING(
        id = "WORK_NOTES",
        title = "Notes & Planning",
        subtitle = "Notion, Google Calendar, Keep & Tasks",
        accentColor = Color(0xFF81C784),
        packageHints = listOf(
            "notion.id", "com.google.android.calendar", "com.google.android.keep",
            "com.miui.notes", "md.obsidian", "com.todoist", "com.ticktick.task",
            "com.google.android.apps.tasks"
        )
    )
}

/**
 * Entertainment Room Real TV Channel Categories.
 * Note: Does not fake live broadcasts; maps channels to real installed streaming/TV/music apps.
 */
enum class TvChannelCategory(
    val id: String,
    val title: String,
    val badgeText: String,
    val accentColor: Color,
    val packageHints: List<String>
) {
    LIVE_TV(
        id = "TV_LIVE",
        title = "Live TV",
        badgeText = "Broadcasters & Live Sports Apps",
        accentColor = Color(0xFF29B6F6),
        packageHints = listOf(
            "com.jiocinema.tv", "com.jio.media.ondemand", "in.startv.hotstar",
            "com.sonyliv", "com.graymatrix.did", "com.sunnxt.sun.tv", "com.airtel.tv",
            "com.tatasky.binge"
        )
    ),
    STREAMING(
        id = "TV_STREAMING",
        title = "Streaming",
        badgeText = "YouTube, OTT & Series",
        accentColor = Color(0xFFEF5350),
        packageHints = listOf(
            "com.google.android.youtube", "com.netflix.mediaclient", "com.amazon.avod.thirdpartyclient",
            "in.startv.hotstar", "com.jio.media.ondemand", "com.sonyliv", "tv.twitch.android.app"
        )
    ),
    MOVIES(
        id = "TV_MOVIES",
        title = "Movies",
        badgeText = "Google TV, VLC, Letterboxd & Cinema",
        accentColor = Color(0xFFFFCA28),
        packageHints = listOf(
            "com.google.android.videos", "org.videolan.vlc", "com.letterboxd.letterboxd",
            "com.mxtech.videoplayer.ad", "com.imdb.mobile", "com.bt.bms"
        )
    ),
    MUSIC(
        id = "TV_MUSIC",
        title = "Music",
        badgeText = "YouTube Music, Spotify & Audio",
        accentColor = Color(0xFF66BB6A),
        packageHints = listOf(
            "com.google.android.apps.youtube.music", "com.spotify.music", "com.jio.media.jiobeats",
            "com.gaana", "com.bsbportal.music", "com.apple.android.music", "com.miui.player"
        )
    ),
    MY_LIST(
        id = "TV_MY_LIST",
        title = "My List",
        badgeText = "Your Custom Pinned TV Apps",
        accentColor = Color(0xFFAB47BC),
        packageHints = emptyList()
    )
}

/**
 * Interactive furniture hotspots across the house.
 */
enum class FurnitureHotspotId(
    val room: RoomId,
    val title: String,
    val description: String,
    val accentColor: Color,
    val defaultPackageHints: List<String>
) {
    // Bedroom Hotspots
    LAPTOP_ON_BED(
        room = RoomId.BEDROOM,
        title = "Stickered Laptop",
        description = "AI, Research, Browser & Quick Study",
        accentColor = Color(0xFF4DD0E1),
        defaultPackageHints = listOf(
            "com.openai.chatgpt", "com.anthropic.claude", "com.google.android.apps.bard",
            "com.android.chrome", "org.mozilla.firefox", "com.brave.browser"
        )
    ),
    STUDY_DESK(
        room = RoomId.BEDROOM,
        title = "Study Desk & Monitors",
        description = "Notes, Calendar, Docs & Productivity",
        accentColor = Color(0xFFFFB74D),
        defaultPackageHints = listOf(
            "notion.id", "com.google.android.calendar", "com.google.android.keep",
            "com.google.android.apps.docs", "com.microsoft.teams", "md.obsidian", "com.miui.notes"
        )
    ),
    BEDSIDE_TABLE(
        room = RoomId.BEDROOM,
        title = "Bedside Table & Lamp",
        description = "Alarms, Clock, Weather, Sleep & Wellness",
        accentColor = Color(0xFFFF8A65),
        defaultPackageHints = listOf(
            "com.android.deskclock", "com.google.android.deskclock", "com.miui.weather2",
            "com.noisefit", "fit.cure.android", "com.spotify.music"
        )
    ),
    WARDROBE_CLOSET(
        room = RoomId.BEDROOM,
        title = "Messy Wardrobe",
        description = "Shopping, Style, Gallery & Camera",
        accentColor = Color(0xFF81C784),
        defaultPackageHints = listOf(
            "com.myntra.android", "in.amazon.mShop.android.shopping", "com.flipkart.android",
            "com.miui.gallery", "com.android.camera", "com.instagram.android"
        )
    ),
    GREEN_PORTAL(
        room = RoomId.BEDROOM,
        title = "Interdimensional Portal",
        description = "Quick Jump to Any Room in the House",
        accentColor = Color(0xFF69F0AE),
        defaultPackageHints = emptyList()
    ),

    // Kitchen Cabinet Hotspot
    KITCHEN_CABINET(
        room = RoomId.KITCHEN,
        title = "Kitchen App Cabinet",
        description = "Swiggy, Blinkit, Zepto, Zomato & Quick Commerce",
        accentColor = Color(0xFFFFB74D),
        defaultPackageHints = listOf(
            "in.swiggy.android", "com.grofers.customerapp", "com.zeptoconsumerapp",
            "com.application.zomato", "com.bigbasket.mobileapp", "in.amazon.mShop.android.shopping",
            "com.flipkart.android", "com.myntra.android", "com.dominos"
        )
    ),

    // Bathroom Wellness Cabinet Hotspot
    BATHROOM_CABINET(
        room = RoomId.BATHROOM,
        title = "Bathroom Wellness Cabinet",
        description = "NoiseFit, cult.fit, Weather, Clock & Self-Care",
        accentColor = Color(0xFF4DD0E1),
        defaultPackageHints = listOf(
            "com.noisefit", "fit.cure.android", "com.google.android.apps.fitness",
            "com.mi.health", "com.miui.weather2", "com.google.android.deskclock",
            "com.android.deskclock", "com.getsomeheadspace.android", "com.calm.android",
            "com.clue.android", "com.flo.health"
        )
    ),

    // Entrance Hallway Mailbox & Organizer Hotspot
    ENTRANCE_MAILBOX(
        room = RoomId.ENTRANCE,
        title = "Hallway Mailbox & Organizer",
        description = "Gmail, WhatsApp, Messages, Phone, Maps, Ola & Rapido",
        accentColor = Color(0xFFFFCA28),
        defaultPackageHints = listOf(
            "com.google.android.gm", "com.whatsapp", "com.google.android.apps.messaging",
            "com.android.mms", "com.google.android.dialer", "com.android.contacts",
            "com.instagram.android", "com.google.android.apps.maps", "com.olacabs.customer",
            "com.rapido.passenger", "com.ubercab", "in.redbus.android", "com.digiyatra"
        )
    )
}

/**
 * Represents an actual installed Android application discovered via LauncherApps / PackageManager.
 */
data class InstalledApp(
    val label: String,
    val packageName: String,
    val activityName: String,
    val iconBitmap: Bitmap? = null,
    val categoryHint: AppCategory = AppCategory.OTHER
)

enum class AppCategory(val displayName: String) {
    AI_AND_RESEARCH("AI & Research"),
    WORK_AND_FILES("Work & Files"),
    CREATIVE_TOOLS("Creative & Editing"),
    NOTES_AND_PLANNING("Notes & Planning"),
    ENTERTAINMENT_TV("TV, Streaming & Music"),
    FOOD_AND_SHOPPING("Kitchen: Food & Quick Commerce"),
    WELLNESS_AND_HEALTH("Bathroom: Wellness & Routine"),
    COMMUNICATION_AND_TRAVEL("Entrance: Comms & Ride-Hailing"),
    OTHER("All Installed Apps")
}

/**
 * Ambient color theme preset for room walls & lighting.
 */
enum class HouseThemePreset(val label: String) {
    COZY_NIGHT("Cozy Sitcom Night (Default)"),
    WARM_SUNSET("Lo-Fi Golden Sunset"),
    SCI_FI_NEON("Portal Cyber Teal")
}

/**
 * Persistent settings state controlled by the user and stored via Jetpack DataStore.
 */
data class LauncherSettings(
    val masterAnimationEnabled: Boolean = true,
    val showGirlCharacter: Boolean = true,
    val showPets: Boolean = true,
    val focusModeEnabled: Boolean = false,
    val defaultRoom: RoomId = RoomId.BEDROOM,
    val themePreset: HouseThemePreset = HouseThemePreset.COZY_NIGHT,
    val customNoteText: String = "A better me lives here. ♥",
    val weatherStatusText: String = "26°C • Clear Night",
    val dailyTasks: List<String> = listOf(
        "Finish UI design review",
        " Evening walk with dog",
        "10 min skincare & water"
    ),
    val favoritePackages: Set<String> = emptySet(),
    val recentPackages: List<String> = emptyList(),
    // Maps collection key (HotspotId, WorkroomSubCategory.id, or TvChannelCategory.id) -> Set of packageNames
    val customCollectionPackages: Map<String, Set<String>> = emptyMap()
)
