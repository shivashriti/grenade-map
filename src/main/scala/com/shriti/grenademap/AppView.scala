package com.shriti.grenademap

import scala.swing._

import com.shriti.grenademap.models._
import com.shriti.grenademap.util.UIUtil._
import event.Key._
import event.KeyPressed
import javax.swing.{AbstractAction, Timer}

/**
  * Creates UI of the Application
  */
object AppView extends SimpleSwingApplication {

  val server = new Server

  def top = new MainFrame {
    title = "Grenade Map"

    contents = new Panel {
      preferredSize = panelSize
      focusable = true

      // TODO: Respond to keys to quit, restart or pause the app
      /*
      listenTo(keys)
      reactions += {
        case KeyPressed(_, key, _, _) =>
          onKeyPress(key)
          repaint
      }*/

      override def paint(g: Graphics2D) {
        g setColor bluishGray
        g fillRect(0, 0, size.width, size.height)
        onPaint(g)
      }

      // Timer to draw the latest step at regular intervals
      val timer = new Timer(drawInterval, new AbstractAction() {
        def actionPerformed(e: java.awt.event.ActionEvent) {
          repaint
        }
      })
      timer.start
    }
  }

  def onKeyPress(keyCode: Value) = keyCode match {
    case Space => server.pause()
    case Q => server.quit()
    case R => server.restart()
    case _ => ???
  }

  def onPaint(g: Graphics2D) {
    val view = server.presentView

    // draws empty grenade map for app
    def drawEmptyGrid {
      g.setColor(bluishLighterGray)
      for {
        x <- 0 to gridSize._1 - 1
        y <- 0 to gridSize._2 - 1
        pos = (x, y)
      } g.draw(buildBlock(pos))
    }

    def drawBlast(b: Block) = {
      g.setColor(white)
      drawTarget(b, "X")
    }

    def drawPerson(b: Block) = {
      g.setColor(red)
      drawTarget(b, "P")
    }

    // draws a target inside a block
    def drawTarget(b: Block, fill: String) = {
      g.setFont(blockFont)
      g.drawString(fill,
        (b.position._1 * totalBlockSize) + blockSize / 4,
        ((b.position._2 + 1) * totalBlockSize) - blockMargin / 4
      )
    }

    // draws the server area with any response if any at current step
    def drawServerArea(msgs: Seq[String]) = {
      g.setColor(bluishLighterGray)
      g.draw(new Rectangle(
        serverAreaCoordinates._1,
        serverAreaCoordinates._2,
        sideAreaWidth,
        sideAreaHeight / 2)
      )
      g.setColor(white)
      g.setFont(messageFont)

      ("SERVER:" +: msgs)
        .foldLeft(serverAreaCoordinates._2 + 3 * totalBlockSize)( (height, line)  => {
          g.drawString(line, serverAreaCoordinates._1 + 3 * totalBlockSize, height)
          height + g.getFontMetrics.getHeight
        })
    }

    // draws the client area with any targets if any at current step
    def drawClientArea(msgs: Seq[String]) = {
      g.setColor(bluishLighterGray)
      g.draw(new Rectangle(
        clientAreaCoordinates._1,
        clientAreaCoordinates._2,
        sideAreaWidth,
        sideAreaHeight / 2)
      )
      g.setColor(white)
      g.setFont(messageFont)

      ("CLIENT:" +: msgs)
        .foldLeft(clientAreaCoordinates._2 + 3 * totalBlockSize)( (height, line)  => {
          g.drawString(line, clientAreaCoordinates._1 + 3 * totalBlockSize, height)
          height + g.getFontMetrics.getHeight
        })
    }

    drawEmptyGrid
    view.blastBlocks.map(drawBlast)
    view.persons.map(drawPerson)
    drawClientArea(view.clientMessages)
    drawServerArea(view.serverResponses)
  }
}
