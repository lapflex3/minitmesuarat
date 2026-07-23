package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LetterheadSettingsScreen
import com.example.ui.screens.LiveRecordingScreen
import com.example.ui.screens.MeetingMinuteDetailScreen
import com.example.ui.screens.NewMeetingSetupScreen
import com.example.ui.viewmodel.MeetingViewModel

object Routes {
    const val DASHBOARD = "dashboard"
    const val SETUP = "setup"
    const val RECORDING = "recording"
    const val DETAIL = "detail/{meetingId}"
    const val LETTERHEAD = "letterhead"

    fun detail(meetingId: Long) = "detail/$meetingId"
}

@Composable
fun MinitAuraNavigation(
    viewModel: MeetingViewModel = viewModel()
) {
    val navController = rememberNavController()

    val meetings by viewModel.meetingsList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val letterhead by viewModel.letterheadState.collectAsState()
    val currentMeeting by viewModel.currentMeeting.collectAsState()

    val isRecording by viewModel.audioRecorder.isRecording.collectAsState()
    val isPaused by viewModel.audioRecorder.isPaused.collectAsState()
    val elapsedSeconds by viewModel.audioRecorder.elapsedSeconds.collectAsState()
    val amplitude by viewModel.audioRecorder.currentAmplitude.collectAsState()
    val liveSpeechText by viewModel.audioRecorder.liveSpeechText.collectAsState()

    val isProcessingAi by viewModel.isProcessingAi.collectAsState()
    val processingMessage by viewModel.processingMessage.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.DASHBOARD
    ) {
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                meetings = meetings,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.searchQuery.value = it },
                onNewMeetingClick = { navController.navigate(Routes.SETUP) },
                onMeetingClick = { meetingId ->
                    viewModel.loadMeeting(meetingId)
                    navController.navigate(Routes.detail(meetingId))
                },
                onExportPdfClick = { meeting ->
                    viewModel.exportCurrentMeetingToPdf(meeting)
                },
                onDeleteMeetingClick = { meetingId ->
                    viewModel.deleteMeeting(meetingId)
                },
                onOpenLetterheadClick = {
                    navController.navigate(Routes.LETTERHEAD)
                }
            )
        }

        composable(Routes.SETUP) {
            NewMeetingSetupScreen(
                onBackClick = { navController.popBackStack() },
                onStartRecordingClick = { title, date, time, location, participants, glossary, chairman, secretary ->
                    viewModel.prepareNewMeeting(
                        title, date, time, location, participants, glossary, chairman, secretary
                    )
                    viewModel.startRecording()
                    navController.navigate(Routes.RECORDING) {
                        popUpTo(Routes.SETUP) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.RECORDING) {
            LiveRecordingScreen(
                currentMeeting = currentMeeting,
                isRecording = isRecording,
                isPaused = isPaused,
                elapsedSeconds = elapsedSeconds,
                amplitude = amplitude,
                liveSpeechText = liveSpeechText,
                isProcessingAi = isProcessingAi,
                processingMessage = processingMessage,
                onPauseClick = { viewModel.audioRecorder.pauseRecording() },
                onResumeClick = { viewModel.audioRecorder.resumeRecording() },
                onStopAndProcessClick = {
                    viewModel.stopAndProcessMeeting()
                    // Delay navigate until processing finishes or navigate immediately
                    val activeId = currentMeeting?.id ?: 0L
                    navController.navigate(Routes.detail(activeId)) {
                        popUpTo(Routes.RECORDING) { inclusive = true }
                    }
                },
                onCancelClick = {
                    viewModel.audioRecorder.stopRecording()
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("meetingId") { type = NavType.LongType })
        ) { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getLong("meetingId") ?: 0L
            if (currentMeeting?.id != meetingId) {
                viewModel.loadMeeting(meetingId)
            }

            MeetingMinuteDetailScreen(
                meeting = currentMeeting,
                letterhead = letterhead,
                onBackClick = { navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) { inclusive = true } } },
                onSaveEdits = { updatedMeeting ->
                    viewModel.updateCurrentMeeting(updatedMeeting)
                },
                onExportPdfClick = { meeting ->
                    viewModel.exportCurrentMeetingToPdf(meeting)
                }
            )
        }

        composable(Routes.LETTERHEAD) {
            LetterheadSettingsScreen(
                letterhead = letterhead,
                onBackClick = { navController.popBackStack() },
                onSaveClick = { companyName, tagline, address, phone, email, refPrefix ->
                    viewModel.saveLetterhead(companyName, tagline, address, phone, email, refPrefix)
                }
            )
        }
    }
}
