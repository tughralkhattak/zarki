package com.zarki.app.ui

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.zarki.app.ui.about.AboutScreen
import com.zarki.app.ui.backup.BackupScreen
import com.zarki.app.ui.browse.BrowseScreen
import com.zarki.app.ui.browse.CatalogViewModel
import com.zarki.app.ui.browse.SourceCatalogScreen
import com.zarki.app.ui.detail.DetailScreen
import com.zarki.app.ui.downloads.DownloadsScreen
import com.zarki.app.ui.detail.DetailViewModel
import com.zarki.app.ui.history.HistoryScreen
import com.zarki.app.ui.library.LibraryScreen
import com.zarki.app.ui.more.MoreScreen
import com.zarki.app.ui.reader.ReaderScreen
import com.zarki.app.ui.reader.ReaderViewModel
import com.zarki.app.ui.settings.SettingsScreen

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("library", "Library", Icons.Default.LibraryBooks),
    Tab("history", "History", Icons.Default.History),
    Tab("browse", "Browse", Icons.Default.Explore),
    Tab("more", "More", Icons.Default.MoreHoriz),
)

@Composable
fun ZarkiApp() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute in tabs.map { it.route }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        tabs.forEach { tab ->
                            NavigationBarItem(
                                selected = currentRoute == tab.route,
                                onClick = {
                                    nav.navigate(tab.route) {
                                        popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(tab.icon, contentDescription = tab.label) },
                                label = { Text(tab.label) },
                            )
                        }
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = nav,
                startDestination = "library",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
            ) {
                composable("library") {
                    LibraryScreen(onOpenManga = { nav.navigate("manga/$it") })
                }
                composable("history") {
                    HistoryScreen(onOpenManga = { nav.navigate("manga/$it") })
                }
                composable("browse") {
                    BrowseScreen(onOpenSource = { nav.navigate("catalog/$it") })
                }
                composable(
                    route = "catalog/{sourceId}",
                    arguments = listOf(navArgument("sourceId") { type = NavType.StringType }),
                ) { entry ->
                    val sid = entry.arguments?.getString("sourceId").orEmpty()
                    val vm: CatalogViewModel = viewModel(
                        key = "catalog-$sid",
                        factory = factoryFor { CatalogViewModel(sid) },
                    )
                    SourceCatalogScreen(
                        viewModel = vm,
                        onBack = { nav.popBackStack() },
                        onOpenManga = { nav.navigate("manga/$it") },
                    )
                }
                composable("more") {
                    MoreScreen(
                        onOpenDownloads = { nav.navigate("downloads") },
                        onOpenBackup = { nav.navigate("backup") },
                        onOpenSettings = { nav.navigate("settings") },
                        onOpenAbout = { nav.navigate("about") },
                    )
                }
                composable("backup") {
                    SubScreen(title = "Backup & restore", onBack = { nav.popBackStack() }) { BackupScreen() }
                }
                composable("settings") {
                    SubScreen(title = "Settings", onBack = { nav.popBackStack() }) { SettingsScreen() }
                }
                composable("downloads") {
                    SubScreen(title = "Downloads", onBack = { nav.popBackStack() }) {
                        DownloadsScreen(onOpenChapter = { chapterId, title ->
                            nav.navigate("reader/$chapterId?title=${Uri.encode(title)}")
                        })
                    }
                }
                composable("about") {
                    SubScreen(title = "About", onBack = { nav.popBackStack() }) { AboutScreen() }
                }
                composable(
                    route = "manga/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.StringType }),
                ) { entry ->
                    val id = entry.arguments?.getString("id").orEmpty()
                    val vm: DetailViewModel = viewModel(
                        key = "detail-$id",
                        factory = factoryFor { DetailViewModel(id) },
                    )
                    DetailScreen(
                        viewModel = vm,
                        onBack = { nav.popBackStack() },
                        onOpenChapter = { chapterId, title ->
                            nav.navigate("reader/$chapterId?title=${Uri.encode(title)}")
                        },
                    )
                }
                composable(
                    route = "reader/{chapterId}?title={title}",
                    arguments = listOf(
                        navArgument("chapterId") { type = NavType.StringType },
                        navArgument("title") { type = NavType.StringType; defaultValue = "" },
                    ),
                ) { entry ->
                    val chapterId = entry.arguments?.getString("chapterId").orEmpty()
                    val title = entry.arguments?.getString("title").orEmpty()
                    val vm: ReaderViewModel = viewModel(
                        key = "reader-$chapterId",
                        factory = factoryFor { ReaderViewModel(chapterId) },
                    )
                    ReaderScreen(viewModel = vm, title = title, onBack = { nav.popBackStack() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubScreen(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background,
        ) { content() }
    }
}

/** Tiny helper to build a ViewModelProvider.Factory from a lambda. */
private inline fun <VM : ViewModel> factoryFor(crossinline create: () -> VM) =
    object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = create() as T
    }
