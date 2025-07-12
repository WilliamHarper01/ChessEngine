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
    var piece_in_front_2 = '-'
    if (y-2 >= 0) piece_in_front_2 = state[(y-2)*8+x]
    var piece_in_front_left = '0'
    var piece_in_front_right = '0'
    if (x-1 >= 0)
        piece_in_front_left = state[(y-1)*8+x-1]
    if (x+1 <= 7)
        piece_in_front_right = state[(y-1)*8+x+1]
    var moves = mutableListOf<String>()
    if (piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y]+RowMap[x]+ColumnMap[y-1])
    if (y == 6) {
        if (piece_in_front_2 == '0' && piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y]+RowMap[x]+ColumnMap[y-2])
    }
    if (piece_in_front_left > 'h') moves.add(""+RowMap[x]+ColumnMap[y]+"x"+RowMap[x-1]+ColumnMap[y-1])
    if (piece_in_front_right > 'h') moves.add(""+RowMap[x]+ColumnMap[y]+"x"+RowMap[x+1]+ColumnMap[y-1])

    if (y-1 != 0) return moves

    var promotion_moves = mutableListOf<String>()
    for (move in moves) {
        promotion_moves.add(move+"q")
        promotion_moves.add(move+"r")
        promotion_moves.add(move+"i")
        promotion_moves.add(move+"n")
    }
    return promotion_moves
}

fun BlackPawnPossibleMoves(state: String, x: Int, y: Int): MutableList<String> {
    if (y+1 > 7) return mutableListOf<String>()
    var piece_in_front = state[(y+1)*8+x]
    var piece_in_front_2 = '-'
    if (y+2 <= 7) piece_in_front_2 = state[(y+2)*8+x]
    var piece_in_front_left = '0'
    var piece_in_front_right = '0'
    if (x-1 >= 0)
        piece_in_front_left = state[(y+1)*8+x-1]
    if (x+1 <= 7)
        piece_in_front_right = state[(y+1)*8+x+1]
    var moves = mutableListOf<String>()
    if (piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y]+RowMap[x]+ColumnMap[y+1])
    if (y == 1) {
        if (piece_in_front_2 == '0' && piece_in_front == '0') moves.add(""+RowMap[x]+ColumnMap[y]+RowMap[x]+ColumnMap[y+2])
    }
    if (piece_in_front_left < 'h' && piece_in_front_left != '0') moves.add(""+RowMap[x]+ColumnMap[y]+"x"+RowMap[x-1]+ColumnMap[y+1])
    if (piece_in_front_right < 'h' && piece_in_front_right != '0') moves.add(""+RowMap[x]+ColumnMap[y]+"x"+RowMap[x+1]+ColumnMap[y+1])
    if (y+1 != 7) return moves

    var promotion_moves = mutableListOf<String>()
    for (move in moves) {
        promotion_moves.add(move+"q")
        promotion_moves.add(move+"r")
        promotion_moves.add(move+"b")
        promotion_moves.add(move+"n")
    }
    return promotion_moves
}

fun PieceCascade(state: String,player: Player, pname: String, x: Int, y: Int, dx: Int, dy: Int, depth: Int, mdepth: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    if (depth > mdepth) return moves
    var xc = x+(dx*depth)
    var yc = y+(dy*depth)
    if (xc < 0 || yc < 0) return moves
    if (xc > 7 || yc > 7) return moves

    if (state[(yc)*8+(xc)] == '0') {
        moves.add(pname+RowMap[x]+ColumnMap[y]+RowMap[xc]+ColumnMap[yc])
        moves.addAll(PieceCascade(state, player,pname, x, y, dx, dy, depth+1, mdepth))
        return moves
    }
    if (state[(yc)*8+(xc)] > 'h' && player == Player.WHITE || state[(yc)*8+(xc)] < 'h' && player == Player.BLACK) {
        moves.add(pname+RowMap[x]+ColumnMap[y]+RowMap[xc]+ColumnMap[yc])
        return moves
    }
    else {
        return moves
    }
}

fun BishopPossibleMoves(state: String, player: Player, pname: String, x: Int, y: Int): MutableList<String> {
    var m1 = PieceCascade(state, player, pname, x, y, -1, -1, 1, 7)
    var m2 = PieceCascade(state, player,pname, x, y, -1, 1, 1, 7)
    var m3 = PieceCascade(state, player,pname, x, y, 1, 1, 1, 7)
    var m4 = PieceCascade(state, player,pname, x, y, 1, -1, 1, 7)
    m1.addAll(m2)
    m1.addAll(m3)
    m1.addAll(m4)
    return m1
}

fun RookPossibleMoves(state: String, player: Player, pname: String, x: Int, y: Int): MutableList<String> {
    var m1 = PieceCascade(state, player,pname, x, y, -1, 0, 1, 7)
    var m2 = PieceCascade(state, player,pname, x, y, 1, 0, 1, 7)
    var m3 = PieceCascade(state, player,pname, x, y, 0, -1, 1, 7)
    var m4 = PieceCascade(state, player,pname, x, y, 0, 1, 1, 7)
    m1.addAll(m2)
    m1.addAll(m3)
    m1.addAll(m4)
    return m1
}

fun QueenPossibleMoves(state: String, player: Player, x: Int, y: Int): MutableList<String> {
    var m1 = BishopPossibleMoves(state, player,"q", x, y)
    var m2 = RookPossibleMoves(state, player,"q", x, y)
    m1.addAll(m2)
    return m1
}

fun KingPossibleMoves(state: String, player: Player, x: Int, y: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    var m1 = PieceCascade(state, player,"k", x, y, -1, 0, 1, 1)
    var m2 = PieceCascade(state, player,"k", x, y, 1, 0, 1, 1)
    var m3 = PieceCascade(state, player,"k", x, y, 0, -1, 1, 1)
    var m4 = PieceCascade(state, player,"k", x, y, 0, 1, 1, 1)
    var m5 = PieceCascade(state, player, "k", x, y, -1, -1, 1, 1)
    var m6 = PieceCascade(state, player,"k", x, y, -1, 1, 1, 1)
    var m7 = PieceCascade(state, player,"k", x, y, 1, 1, 1, 1)
    var m8 = PieceCascade(state, player,"k", x, y, 1, -1, 1, 1)
    m1.addAll(m2)
    m1.addAll(m3)
    m1.addAll(m4)
    m1.addAll(m5)
    m1.addAll(m6)
    m1.addAll(m7)
    m1.addAll(m8)
    return m1
}

fun KnightAngle(state: String, player: Player, x: Int, y: Int, dx: Int, dy: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    var xc = x+dx
    var yc = y+dy
    if (xc < 0 || yc < 0) return moves
    if (xc > 7 || yc > 7) return moves
    var square_index = yc*8+xc
    if ((state[square_index] > 'h' && player == Player.WHITE) || (state[square_index] < 'h' && player == Player.BLACK) || (state[square_index] == '0')) {
        moves.add("n"+RowMap[x]+ColumnMap[y]+RowMap[xc]+ColumnMap[yc])
    }
    return moves
}

fun KnightPossibleMoves(state: String, player: Player, x: Int, y: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    moves.addAll(KnightAngle(state, player, x, y, -2, -1))
    moves.addAll(KnightAngle(state, player, x, y, -2, 1))
    moves.addAll(KnightAngle(state, player, x, y, -1, -2))
    moves.addAll(KnightAngle(state, player, x, y, -1, 2))
    moves.addAll(KnightAngle(state, player, x, y, 1, -2))
    moves.addAll(KnightAngle(state, player, x, y, 1, 2))
    moves.addAll(KnightAngle(state, player, x, y, 2, -1))
    moves.addAll(KnightAngle(state, player, x, y, 2, 1))
    return moves
}

fun SquarePossibleMoves(state: String, player: Player, x: Int, y: Int): MutableList<String> {
    var moves = mutableListOf<String>()
    var square_index = y*8+x
    var piece = state[square_index]
    if (piece == '0') return moves
    if (piece == 'a' && player == Player.WHITE) return WhitePawnPossibleMoves(state, x, y)
    if (piece == 'z' && player == Player.BLACK) return BlackPawnPossibleMoves(state, x, y)
    if (piece == 'd' && player == Player.WHITE) return BishopPossibleMoves(state, player, "i", x, y)
    if (piece == 'w' && player == Player.BLACK) return BishopPossibleMoves(state, player,"i", x, y)
    if (piece == 'b' && player == Player.WHITE) return RookPossibleMoves(state, player,"r", x, y)
    if (piece == 'y' && player == Player.BLACK) return RookPossibleMoves(state, player,"r", x, y)
    if (piece == 'e' && player == Player.WHITE) return QueenPossibleMoves(state, player, x, y)
    if (piece == 'v' && player == Player.BLACK) return QueenPossibleMoves(state, player, x, y)
    if (piece == 'f' && player == Player.WHITE) return KingPossibleMoves(state, player, x, y)
    if (piece == 'u' && player == Player.BLACK) return KingPossibleMoves(state, player, x, y)
    if (piece == 'c' && player == Player.WHITE) return KnightPossibleMoves(state, player, x, y)
    if (piece == 'x' && player == Player.BLACK) return KnightPossibleMoves(state, player, x, y)

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

fun MakeMove(state: String, player: Player, algebra: String): String {
    var state_list = state.toMutableList()

    if (algebra[0] == 'q') {
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[1]]!!
        var y1 = ColumnMapInv[simple[2]]!!
        var x2 = RowMapInv[simple[3]]!!
        var y2 = ColumnMapInv[simple[4]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = 'e'
        if (player == Player.BLACK) state_list[index2] = 'v'
    }
    else if (algebra[0] == 'r') {
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[1]]!!
        var y1 = ColumnMapInv[simple[2]]!!
        var x2 = RowMapInv[simple[3]]!!
        var y2 = ColumnMapInv[simple[4]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = 'b'
        if (player == Player.BLACK) state_list[index2] = 'y'
    }
    else if (algebra[0] == 'n') {
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[1]]!!
        var y1 = ColumnMapInv[simple[2]]!!
        var x2 = RowMapInv[simple[3]]!!
        var y2 = ColumnMapInv[simple[4]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = 'c'
        if (player == Player.BLACK) state_list[index2] = 'x'
    }
    else if (algebra[0] == 'i') {
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[1]]!!
        var y1 = ColumnMapInv[simple[2]]!!
        var x2 = RowMapInv[simple[3]]!!
        var y2 = ColumnMapInv[simple[4]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = 'd'
        if (player == Player.BLACK) state_list[index2] = 'w'
    }
    else if (algebra[0] == 'k') {
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[1]]!!
        var y1 = ColumnMapInv[simple[2]]!!
        var x2 = RowMapInv[simple[3]]!!
        var y2 = ColumnMapInv[simple[4]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = 'f'
        if (player == Player.BLACK) state_list[index2] = 'u'
    }
    else {
        var pawn_promotion = 'a'
        if (player == Player.BLACK) pawn_promotion = 'z'
        if (algebra.contains('q')) {
            pawn_promotion = 'e'
            if (player == Player.BLACK) pawn_promotion = 'v'
        }
        if (algebra.contains('r')) {
            pawn_promotion = 'b'
            if (player == Player.BLACK) pawn_promotion = 'y'
        }
        if (algebra.contains('i')) {
            pawn_promotion = 'c'
            if (player == Player.BLACK) pawn_promotion = 'x'
        }
        if (algebra.contains('n')) {
            pawn_promotion = 'd'
            if (player == Player.BLACK) pawn_promotion = 'w'
        }
        var simple = algebra.replace("x", "").replace("+", "")
        var x1 = RowMapInv[simple[0]]!!
        var y1 = ColumnMapInv[simple[1]]!!
        var x2 = RowMapInv[simple[2]]!!
        var y2 = ColumnMapInv[simple[3]]!!
        var index1 = y1*8+x1
        var index2 = y2*8+x2
        state_list[index1] = '0'
        state_list[index2] = pawn_promotion
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

fun BestMoveMaterial(state: String, player: Player, moves: MutableList<String>, depth: Int, mdepth: Int): Int {
    var best = 9999
    var best_indices = mutableListOf<Int>()
    if (player == Player.WHITE) best = -9999
    for (move in 0 ..< moves.size) {
        var new_state = MakeMove(state, player, moves[move])
        var material = GetMaterial(new_state)

        if (depth < mdepth) {
            var new_moves = ListPossibleMoves(new_state, player)
            if (new_moves.isEmpty()) return best
            if (player == Player.WHITE) material = BestMoveMaterial(new_state, Player.BLACK, new_moves, depth + 1, mdepth)
            else material = BestMoveMaterial(new_state, Player.WHITE, new_moves, depth + 1, mdepth)
        }

        if (player == Player.WHITE) {
            if (material > best) {
                best = material
                best_indices.clear()
                best_indices.add(move)
            }
            else if (material == best) {
                best_indices.add(move)
            }
        }
        else {
            if (material < best) {
                best = material
                best_indices.clear()
                best_indices.add(move)
            }
            else if (material == best) {
                best_indices.add(move)
            }
        }
    }
    return best
}

fun BestMove(state: String, player: Player, moves: MutableList<String>, depth: Int, mdepth: Int): Int {
    var best = 9999
    var best_indices = mutableListOf<Int>()
    if (player == Player.WHITE) best = -9999
    for (move in 0 ..< moves.size) {
        var new_state = MakeMove(state, player, moves[move])

        var material = GetMaterial(new_state)
        if (depth < mdepth) {
            var new_moves = ListPossibleMoves(new_state, player)
            if (new_moves.isEmpty()) return best_indices.random()
            if (player == Player.WHITE) material = BestMoveMaterial(new_state, Player.BLACK, new_moves, depth + 1, mdepth)
            else material = BestMoveMaterial(new_state, Player.WHITE, new_moves, depth + 1, mdepth)
        }
        if (player == Player.WHITE) {
            if (material > best) {
                best = material
                best_indices.clear()
                best_indices.add(move)
            }
            else if (material == best) {
                best_indices.add(move)
            }
        }
        else {
            if (material < best) {
                best = material
                best_indices.clear()
                best_indices.add(move)
            }
            else if (material == best) {
                best_indices.add(move)
            }
        }
    }
    return best_indices.random()
}

fun DecideNextMove(state: String, player: Player): String {
    var moves = ListPossibleMoves(state, player)
    if (moves.isEmpty()) return state
    var bestMove = BestMove(state, player, moves, 1, 1)
    if (bestMove == -1) return MakeMove(state, player, moves[0])
    return MakeMove(state, player, moves[bestMove])
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