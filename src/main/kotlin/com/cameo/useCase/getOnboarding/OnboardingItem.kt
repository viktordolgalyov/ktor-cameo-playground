package com.cameo.useCase.getOnboarding

data class OnboardingItem(val movie: OnboardingMovieItem?,
                          val series: OnboardingSeriesItem?)

data class OnboardingMovieItem(val id: Int,
                               val title: String,
                               val genres: String,
                               val poster: String)

data class OnboardingSeriesItem(val id: Int,
                                val title: String,
                                val genres: String,
                                val poster: String)