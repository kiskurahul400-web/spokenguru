package com.spokenguru.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spokenguru.app.data.repository.LessonRepository
import com.spokenguru.app.ui.screens.ImportLessonScreen
import com.spokenguru.app.ui.screens.LessonListScreen
import com.spokenguru.app.ui.screens.MistakeReviewScreen
import com.spokenguru.app.ui.screens.WordPickScreen
import com.spokenguru.app.ui.theme.SpokenGuruTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: LessonRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        repository = LessonRepository(applicationContext)

        lifecycleScope.launch {
            repository.ensureDataSeeded()
        }

        setContent {
            SpokenGuruTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "lessons"
                    ) {
                        composable("lessons") {
                            LessonListScreen(
                                repository = repository,
                                onSelectLesson = { lessonNumber ->
                                    navController.navigate("quiz/$lessonNumber")
                                },
                                onOpenMistakes = {
                                    navController.navigate("mistakes")
                                },
                                onOpenImport = {
                                    navController.navigate("import")
                                }
                            )
                        }

                        composable(
                            route = "quiz/{lessonNumber}",
                            arguments = listOf(
                                navArgument("lessonNumber") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val lessonNumber = backStackEntry.arguments?.getInt("lessonNumber") ?: 1
                            WordPickScreen(
                                lessonNumber = lessonNumber,
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("mistakes") {
                            MistakeReviewScreen(
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("import") {
                            ImportLessonScreen(
                                repository = repository,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
