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
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

enum class Player {
    WHITE,
    BLACK
}

val RowMap = mapOf(0 to "a", 1 to "b", 2 to "c", 3 to "d", 4 to "e", 5 to "f", 6 to "g", 7 to "h")
val RowMapInv = mapOf('a' to 0, 'b' to 1, 'c' to 2, 'd' to 3, 'e' to 4, 'f' to 5, 'g' to 6, 'h' to 7)
val ColumnMap = mapOf(0 to "8", 1 to "7", 2 to "6", 3 to "5", 4 to "4", 5 to "3", 6 to "2", 7 to "1")
val ColumnMapInv = mapOf('8' to 0, '7' to 1, '6' to 2, '5' to 3, '4' to 4, '3' to 5, '2' to 6, '1' to 7)

val MaterialMap = mapOf('a' to 1, 'b' to 3, 'c' to 3, 'd' to 5, 'e' to 9, 'f' to 1000,'z' to -1, 'y' to -3, 'x' to -3, 'w' to -5, 'v' to -9, 'u' to -1000, '0' to 0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChessEngineTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Board()
                }
            }
        }
    }
}

fun WhitePawnPossibleMoves(state: String, x: Int, y: Int): MutableList<String> {
    if (y-1 < 0) return mutableListOf<String>()
    var piece_in_front = state[(y-1)*8+x]
    var piece_in_front_2 = state[(y-2)*8+x]
    var piece_in_front_left = state[(y-1)*8+x-1]
    var piece_in_front_right = state[(y-1)*8+x+1]
    var moves = mutableListOf<String>()
    if (piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y-1])
    if (y == 6) {
        if (piece_in_front_2 == '0') moves.add(""+RowMap[x]+ColumnMap[y-2])
    }
    if (piece_in_front_left > 'h') moves.add(""+RowMap[x]+"x"+RowMap[x-1]+ColumnMap[y-1])
    if (piece_in_front_right > 'h') moves.add(""+RowMap[x]+"x"+RowMap[x+1]+ColumnMap[y-1])
    return moves
}

fun BlackPawnPossibleMoves(state: String, x: Int, y: Int): MutableList<String> {
    if (y+1 > 7) return mutableListOf<String>()
    var piece_in_front = state[(y+1)*8+x]
    var piece_in_front_2 = state[(y+2)*8+x]
    var piece_in_front_left = state[(y+1)*8+x-1]
    var piece_in_front_right = state[(y+1)*8+x+1]
    var moves = mutableListOf<String>()
    if (piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y+1])
    if (y == 1) {
        if (piece_in_front_2 == '0') moves.add(""+RowMap[x]+ColumnMap[y+2])
    }
    if (piece_in_front_left < 'h' && piece_in_front_left != '0') moves.add(""+RowMap[x]+"x"+RowMap[x-1]+ColumnMap[y+1])
    if (piece_in_front_right < 'h' && piece_in_front_right != '0') moves.add(""+RowMap[x]+"x"+RowMap[x+1]+ColumnMap[y+1])
    return moves
}


fun SquarePossibleMoves(state: String, player: Player, x: Int, y: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    var square_index = y*8+x
    var piece = state[square_index]
    if (piece == '0') return moves
    if (piece == 'a' && player == Player.WHITE) return WhitePawnPossibleMoves(state, x, y)
    if (piece == 'z' && player == Player.BLACK) return BlackPawnPossibleMoves(state, x, y)
    return moves
}

fun ListPossibleMoves(state: String, player: Player): MutableList<String> {
    var moves = mutableListOf<String>()
    for (i in 0..7) {
        for (j in 0..7) {
            moves.addAll(SquarePossibleMoves(state, player, j, i))
        }
    }
    return moves
}

fun InferPawnStart(state: String, player: Player, x: Int, y: Int): Int {
    var ys = y
    while(true) {
        if (player == Player.WHITE) ys += 1
        else ys -= 1
        var index = ys*8+x
        if (state[index] != '0') return index
    }
}

fun MakeMove(state: String, player: Player, algebra: String): String {
    var state_list = state.toMutableList()
    if (algebra.length == 2) {
        var x = RowMapInv[algebra[0]]!!
        var y = ColumnMapInv[algebra[1]]!!
        var square_index = y*8+x
        if (player == Player.WHITE) state_list[square_index] = 'a'
        if (player == Player.BLACK) state_list[square_index] = 'z'
        state_list[InferPawnStart(state, player, x, y)] = '0'
    }
    if (algebra[1] == 'x') {
        var x = RowMapInv[algebra[2]]!!
        var y = ColumnMapInv[algebra[3]]!!
        var square_index = y*8+x
        if (player == Player.WHITE) state_list[square_index] = 'a'
        else state_list[square_index] = 'z'
        x = RowMapInv[algebra[0]]!!
        if (player == Player.WHITE) y+=1
        else y-=1
        var index = y*8+x
        state_list[index] = '0'
    }
    return state_list.joinToString("")
}

fun GetMaterial(state: String): Int {
    var material = 0
    for (piece in state) {
        material += MaterialMap[piece]!!
    }
    return material
}

fun DecideNextMove(state: String, player: Player): String {
    var moves = ListPossibleMoves(state, player)
    if (moves.isEmpty()) return state
    var best = 9999
    var best_index = 0
    if (player == Player.WHITE) best = -9999
    for (move in 0 ..< moves.size) {
        var new_state = MakeMove(state, player, moves[move])
        var material = GetMaterial(new_state)
        if (player == Player.WHITE) {
            if (material > best) {
                best = material
                best_index = move
            }
        }
        else {
            if (material < best) {
                best = material
                best_index = move
            }
        }
    }
    return MakeMove(state, player, moves[best_index])
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
var CurrentPlayer = Player.WHITE

    @Composable
fun Board() {
    var state by remember {mutableStateOf(start)}
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
        Button( content = { Text("Next Move") },
            onClick = { state = DecideNextMove(state, CurrentPlayer)
            if (CurrentPlayer == Player.WHITE) CurrentPlayer = Player.BLACK
            else CurrentPlayer = Player.WHITE })
        Button( content = { Text("Reset") },
            onClick = { state = start
            CurrentPlayer = Player.WHITE })
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
        Board()
    }
}