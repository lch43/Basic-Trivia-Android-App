package lch43.trivia

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.net.URLDecoder
import java.util.*
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var correct = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val creditButton = findViewById<Button>(R.id.creditButton)
        creditButton.setOnClickListener {
            val openURL = Intent(android.content.Intent.ACTION_VIEW)
            openURL.data = Uri.parse("https://opentdb.com/")
            startActivity(openURL)
        }

        loadNewQuestion()
    }

    fun submitAnswer(view: View)
    {
        if (correct != -1)
        {
            var ID = 0
            var views = arrayOf(findViewById<Button>(R.id.answerOne), findViewById<Button>(R.id.answerTwo), findViewById<Button>(R.id.answerThree), findViewById<Button>(R.id.answerFour))
            if (view.equals(views[0])){ID = 0}
            else if (view.equals(views[1])){ID = 1}
            else if (view.equals(views[2])){ID = 2}
            else if (view.equals(views[3])){ID = 3}

            val responseText = findViewById<TextView>(R.id.answerStatus)

            if (ID == correct) //Right
            {
                responseText.text = "Correct! :)"
            }
            else //Wrong
            {
                responseText.text = "Incorrect :("
            }
            views[correct].text = "Correct: "+views[correct].text
            correct = -1
            Thread{
                Thread.sleep(2000)
                loadNewQuestion()
            }.start()
        }
    }

    private fun loadNewQuestion() {
        val text = findViewById<TextView>(R.id.questionText)
        val answerStatus = findViewById<TextView>(R.id.answerStatus)
        val a1 = findViewById<Button>(R.id.answerOne)
        val a2 = findViewById<Button>(R.id.answerTwo)
        val a3 = findViewById<Button>(R.id.answerThree)
        val a4 = findViewById<Button>(R.id.answerFour)

        //Get request test
        var queue = Volley.newRequestQueue(this)
        val url = "https://opentdb.com/api.php?amount=1&type=multiple&encode=url3986"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                if (response.getInt("response_code") == 0) {
                    val results = response.getJSONArray("results").getJSONObject(0)
                    text.text = URLDecoder.decode(results.getString("question").toString(), "UTF-8")
                    val correctAnswer = URLDecoder.decode(results.getString("correct_answer"), "UTF-8")
                    val wrongAnswers = results.getJSONArray("incorrect_answers")

                    val startIndex = Random.nextInt(0,4)

                    for (x in 0..3) {
                        val currIndex = (startIndex + x) % 4
                        var currVal = ""
                        if (x<1)
                        {
                            currVal = correctAnswer
                            correct = currIndex
                        }
                        else if (wrongAnswers.length() > x-1)
                        {
                            currVal =  URLDecoder.decode(wrongAnswers.get(x-1).toString(), "UTF-8")
                        }

                        if (currIndex == 0)
                        {
                            a1.text = currVal
                        }
                        else if(currIndex == 1)
                        {
                            a2.text = currVal
                        }
                        else if(currIndex == 2)
                        {
                            a3.text = currVal
                        }
                        else if(currIndex == 3)
                        {
                            a4.text = currVal
                        }
                    }
                }
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            }
        )
        queue.add(jsonObjectRequest)

        a1.visibility = View.VISIBLE
        a2.visibility = View.VISIBLE
        a3.visibility = View.VISIBLE
        a4.visibility = View.VISIBLE
        answerStatus.text = ""
    }
}