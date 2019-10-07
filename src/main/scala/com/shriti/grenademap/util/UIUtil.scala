package com.shriti.grenademap.util

import com.shriti.grenademap.models.gridSize

import scala.swing.{Color, Dimension, Font, Rectangle}

/**
  * Utility constants/methods to create UI of application
  */
private[grenademap] object UIUtil {

  // app panel size
  val panelSize = new Dimension(1100, 800)

  // grid area and side area details
  val blockSize = 12
  val blockMargin = 1
  val totalBlockSize = blockSize + blockMargin
  val gridWidth = totalBlockSize * gridSize._1
  val gridHeight = totalBlockSize * gridSize._2
  val sideAreaWidth = panelSize.width - gridWidth - 2 * totalBlockSize
  val sideAreaHeight = gridHeight - 2 * totalBlockSize

  // colors
  val bluishGray = new Color(48, 99, 99)
  val bluishLighterGray = new Color(79, 130, 130)
  val white = new Color(255, 255, 255)
  val red = new Color(255, 0, 0)

  // fonts
  val blockFont = new Font("Arial", 0, 12)
  val messageFont = new Font("Arial", 0, 16)

  val clientAreaCoordinates =
    (gridWidth + totalBlockSize, totalBlockSize)

  val serverAreaCoordinates =
    (gridWidth + totalBlockSize, 2 * totalBlockSize + sideAreaHeight / 2)

  def buildBlock(pos: (Int, Int)): Rectangle =
    new Rectangle(
      pos._1 * totalBlockSize,
      pos._2 * totalBlockSize,
      blockSize, blockSize)
}
