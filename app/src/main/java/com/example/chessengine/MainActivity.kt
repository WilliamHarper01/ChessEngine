package com.example.chessengine

import androidx.compose.foundation.Image
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.chessengine.ui.theme.ChessEngineTheme
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessEngineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Board(start)
                }
            }
        }
    }
}

var size = 360
val piecemap = mapOf('a' to R.drawable.wp,
    'b' to R.drawable.wr,
    'c' to R.drawable.wn,
    'd' to R.drawable.wb,
    'e' to R.drawable.wq,
    'f' to R.drawable.wk,
    'z' to R.drawable.bp,
    'y' to R.drawable.br,
    'x' to R.drawable.bn,
    'w' to R.drawable.bb,
    'v' to R.drawable.bq,
    'u' to R.drawable.bk,
    '0' to R.drawable.blank)
val start = "yxwvuwxyzzzzzzzz00000000000000000000000000000000aaaaaaaabcdefdcb"


@Composable
fun Board(state: String) {
    Image(painterResource(id = R.drawable.board),
        contentDescription = "",
        modifier = Modifier.size(size.dp))
    Column {
        for (i in 0..7) {
            Row {
                for (j in 0..7) {
                    if (state.length <= i*8+j) continue
                    val piece = piecemap[state[i*8+j]]
                    Image(painterResource(id = piece!!),
                        contentDescription = "",
                        modifier = Modifier.size((size/8).dp))
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Peepo $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ChessEngineTheme {
        Board(start)
    }
}