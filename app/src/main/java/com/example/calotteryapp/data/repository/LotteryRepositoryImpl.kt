package com.example.calotteryapp.data.repository

import android.util.Log
import com.example.calotteryapp.data.local.LotteryDao
import com.example.calotteryapp.data.mappers.toLotteryDraw
import com.example.calotteryapp.data.mappers.toLotteryDrawEntity
import com.example.calotteryapp.data.remote.LotteryApi
import com.example.calotteryapp.di.IoDispatcher
import com.example.calotteryapp.domain.model.LotteryDraw
import com.example.calotteryapp.domain.repository.LotteryRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class LotteryRepositoryImpl @Inject constructor(
    private val lotteryDao: LotteryDao,
    private val lotteryApi: LotteryApi,
    private val simpleDateFormat: SimpleDateFormat,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LotteryRepository {
    override suspend fun updateLotteryResults() {
        try {
            val currentDateString = simpleDateFormat.format(Date())
            val currentDate = simpleDateFormat.parse(currentDateString)

            val mostRecentDraw = lotteryDao.getLotteryDrawByDate(currentDate)

            if (mostRecentDraw != null) return

            val apiResponse: Response<String> = withContext(ioDispatcher) {
                lotteryApi.getWebsiteHTML()
            }
            val doc: Document = Jsoup.parse(apiResponse.body())

            val dateElement = doc.getElementsByClass("draw-cards--draw-date")
            val dateText = dateElement.text()
                .split("/")
                .last()
            val dateOfDraw = simpleDateFormat.parse(dateText)

            val idElement = doc.getElementsByClass("draw-cards--draw-number")
            val id = idElement.text()
                .split(" ")
                .last()
                .removePrefix("#")
                .toInt()

            val winningNumbersElements =
                doc.getElementsByClass("list-inline draw-cards--winning-numbers")
            val listOfWinningNumbers = winningNumbersElements[0].text()
                .trim()
                .split(" ")
                .dropLast(1)        // last element shows "powerball"
                .map { it.toInt() }    // convert to int list

            val prizeTableElements =
                doc.getElementsByClass("table table-striped table-last-draw")
            val prizeAmountString = prizeTableElements
                .select("table tbody tr")[0]
                .text()
                .split(" ")
                .last()                // desired element is last in list

            val prizeAmountInt = prizeAmountString
                .filterNot { "$,".indexOf(it) > -1 }
                .toInt()

            val lotteryDrawScraped = LotteryDraw(
                id = id,
                date = dateOfDraw,
                winningNumbers = listOfWinningNumbers,
                prizeAmount = prizeAmountInt
            )

            lotteryDao.insertLotteryDraw(
                lotteryDrawScraped.toLotteryDrawEntity()
            )
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
        }
    }

    override suspend fun getLotteryResultsInDb(): List<LotteryDraw> {
        return lotteryDao.getLotteryDraws()?.map {
            it.toLotteryDraw()
        } ?: listOf()
    }

    companion object {
        const val TAG = "LotteryRepositoryImpl"
    }

}
