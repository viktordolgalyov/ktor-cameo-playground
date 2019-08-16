package com.cameo.useCase.getSchedule

data class UserScheduleResponse(val items: List<ScheduleItem>,
                                val minDate: String,
                                val maxDate: String)