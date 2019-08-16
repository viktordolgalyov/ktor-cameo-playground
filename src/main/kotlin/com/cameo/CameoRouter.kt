package com.cameo

import com.cameo.controller.*
import com.cameo.useCase.followItem.FollowItemUseCase
import com.cameo.useCase.getFeed.GetFeedUseCase
import com.cameo.useCase.getMovieDetails.GetMovieDetailsUseCase
import com.cameo.useCase.getMovieTrailers.GetMovieTrailersUseCase
import com.cameo.useCase.getOnboarding.GetOnboardingUseCase
import com.cameo.useCase.getSchedule.GetUserScheduleUseCase
import com.cameo.useCase.getSearchItems.GetSearchItemsUseCase
import com.cameo.useCase.getSeasons.GetSeasonsUseCase
import com.cameo.useCase.getSeriesDetails.GetSeriesDetailsUseCase
import com.cameo.useCase.getSeriesTrailers.GetSeriesTrailersUseCase
import com.cameo.useCase.getUserList.GetUserListUseCase
import com.cameo.useCase.loginUser.LoginUserUseCase
import com.cameo.useCase.markEpisodeNotWatched.MarkEpisodeNotWatchedUseCase
import com.cameo.useCase.markEpisodeWatched.MarkEpisodeWatchedUseCase
import com.cameo.useCase.markSeasonNotWatched.MarkSeasonNotWatchedUseCase
import com.cameo.useCase.markSeasonWatched.MarkSeasonWatchedUseCase
import com.cameo.useCase.registerUser.SignUpUserUseCase
import com.cameo.useCase.unfollowItem.UnfollowItemUseCase
import io.ktor.routing.Routing
import io.ktor.routing.route

fun Routing.proceed() {

    route("/series") { seriesController().proceed(this) }
    route("/movies") { moviesController().proceed(this) }
    route("/user") { userController().proceed(this) }
    route("/feed") { feedController().proceed(this) }
    route("/schedule") { scheduleController().proceed(this) }
    route("/search") { searchController().proceed(this) }
    route("/onboarding") { onboardingController().proceed(this) }
    route("/seasons") { seasonsController().proceed(this) }
    route("/episodes") { episodesController().proceed(this) }
}

private fun seriesController() = SeriesController(GetSeriesDetailsUseCase(), GetSeriesTrailersUseCase())

private fun moviesController() = MoviesController(GetMovieDetailsUseCase(), GetMovieTrailersUseCase())

private fun userController() = UserController(LoginUserUseCase(), SignUpUserUseCase(), GetUserListUseCase())

private fun feedController() = FeedController(GetFeedUseCase())

private fun scheduleController() = ScheduleController(GetUserScheduleUseCase(), FollowItemUseCase(), UnfollowItemUseCase())

private fun searchController() = SearchController(GetSearchItemsUseCase())

private fun onboardingController() = OnboardingController(GetOnboardingUseCase())

private fun seasonsController() = SeasonsController(GetSeasonsUseCase(), MarkSeasonWatchedUseCase(), MarkSeasonNotWatchedUseCase())

private fun episodesController() = EpisodesController(MarkEpisodeWatchedUseCase(), MarkEpisodeNotWatchedUseCase())