import java.awt.Color
import java.awt.*
import java.awt.event.*
import java.util.*
import javax.swing.*
import kotlin.concurrent.thread
fun main(args: Array<String>) {
    val window = JFrame("Very Simple Snake Game in Kotlin")
    window.setSize(500, 500)
    window.setLocationRelativeTo(null)
    window.isVisible = true
    window.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    class Vec(var x: Int, var y: Int) {}
    var running = true
    val snake = ArrayDeque<Vec>()
    for (i in 0..5) snake.push(Vec(100 + i * 25, 100))
    val feed = LinkedList<Vec>()
    var dx = 1
    var dy = 0
    var score = 0
    window.add(object : JPanel() {
        init {
            background = Color.black;
        }
        override fun paint(g: Graphics?) {
            super.paint(g)
            g!!.font = Font("Courier New", Font.BOLD, 25)
            g.color = Color.WHITE
            val head = snake.peekFirst()
            var x = head.x + dx * 25
            var y = head.y + dy * 25
            x = if (x - 10 > window.width) 0 else if (x + 10 < 0) window.width else x
            y = if (y - 10 > window.height) 0 else if (y + 10 < 0) window.height else y
            val next = Vec(x, y)
            snake.removeLast()
            snake.addFirst(next)
            thread {
                for (i in 0 until feed.size) {
                    val f = feed[i]
                    if (x < f.x + 10 && x + 20 > f.x && y < f.y + 10 && y + 20 > f.y) {
                        score++
                        val tail = snake.peekLast()
                        snake.addLast(Vec(tail.x, tail.y))
                        feed.remove(f)
                        break
                    }
                }
            }
            feed.forEach { f -> g.fillRect(f.x, f.y, 10, 10) }
            snake.forEach { s ->
                g.fillRect(s.x, s.y, 20, 20)
                if (s != next && x < s.x + 20 && x + 20 > s.x && y < s.y + 20 && y + 20 > s.y) {
                    running = false
                    g.color = Color.RED
                    g.fillRect(x, y, 20, 20)
                    g.color = Color.WHITE
                }
            }
            g.drawString("Score : $score", 50, 50)
            if (!running) g.drawString("Game Over", width / 2 - 55, height / 2)
        }
    })
    window.addKeyListener(object : KeyListener {
        override fun keyTyped(e: KeyEvent?) {}
        override fun keyReleased(e: KeyEvent?) {}
        override fun keyPressed(e: KeyEvent?) {
            when (e!!.keyCode) {
                KeyEvent.VK_W -> {
                    dx = 0
                    dy = -1
                }
                KeyEvent.VK_S -> {
                    dx = 0
                    dy = 1
                }
                KeyEvent.VK_A -> {
                    dy = 0
                    dx = -1
                }
                KeyEvent.VK_D -> {
                    dy = 0
                    dx = 1
                }
            }
        }
    })
    thread {
        while (running) {
            window.repaint()
            Thread.sleep(1000 / 10)
        }
    }
    thread {
        fun ClosedRange<Int>.rand() = Random().nextInt(endInclusive - start) + start
        while (running) {
            feed.push(Vec((0..window.width).rand(), (0..window.height).rand()))
            Thread.sleep(2000)
        }
    }
}