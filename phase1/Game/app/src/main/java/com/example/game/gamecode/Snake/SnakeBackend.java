package com.example.game.gamecode.Snake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import androidx.annotation.NonNull;

import com.example.game.gamecode.GameBackend;
import com.example.game.gamecode.GameObject;

import java.util.ArrayList;
import java.util.Random;

public class SnakeBackend extends GameBackend {
  private SnakeHead snakeHead;
  private boolean lost;

  /** The number of apples eaten. */
  private int apples;

  /** The distance that the snake traveled (in grids). */
  private int distance;

  /** The length of the snake. */
  private int snakeLength = 1;

  /** The size of each component */
  private int size;

  /** The width of Snake. */
  private int gridWidth;

  /** The height of Snake. */
  private int gridHeight;

  /** The shape of its snake objects. */
  private SnakeShape shape = SnakeShape.CIRCLE;

  /** The color of the background */
  private int canvasColor = Color.BLACK;

  public ArrayList<GameObject> getGameObjects() {
    return gameObjects;
  }

  private void addSnakeObj(SnakeObject g) {
    this.gameObjects.add(g);
  }

  private void setLost(boolean lost) {
    this.lost = lost;
  }

  public int getGridWidth() {
    return gridWidth;
  }

  public int getGridHeight() {
    return gridHeight;
  }

  SnakeBackend(int h, int w) {
    gameObjects = new ArrayList<>();
    lost = false;
    this.size = (int) Double.min(h / 64, w / 64);
    gridHeight = h / size;
    gridWidth = w / size;
  }

  SnakeBackend(int h, int w, SnakeShape shape) {
    gameObjects = new ArrayList<>();
    lost = false;
    this.size = (int) Double.min(h / 64, w / 64);
    gridHeight = h / size;
    gridWidth = w / size;
    this.shape = shape;
  }

  @Override
  public void draw(Canvas canvas) {
    drawBackground(canvas);
    for (int a = 0; a < gameObjects.size(); a++) {
      if (gameObjects.get(a) != null) {
        gameObjects.get(a).draw(canvas);
      }
    }
  }

  public void setHeadColor(int color) {
    this.snakeHead.setColor(color);
  }

  public void setAppleColor(int color) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject instanceof Apple) {
        ((Apple) gameObject).setColor(color);
      }
    }
  }

  public void setWallColor(int color) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject instanceof Wall) {
        ((Wall) gameObject).setColor(color);
      }
    }
  }

  public void setBodyColor(int color) {
    for (GameObject gameObject : gameObjects) {
      if (gameObject instanceof SnakeComponent && !(gameObject instanceof SnakeHead)) {
        ((SnakeComponent) gameObject).setColor(color);
      }
    }
  }

  @Override
  public void update() {
    snakeHead.move();
    boolean eatApple = false;

    for (GameObject gameObject : gameObjects) {
      if (gameObject instanceof Apple) {
        Apple apple = (Apple) gameObject;
        if (snakeHead.atPosition(apple.x, apple.y)) {
          eatApple(apple);
          eatApple = true;
        }
      } else if (gameObject instanceof Wall || gameObject instanceof SnakeComponent) {
        SnakeObject snakeObject = (SnakeObject) gameObject;
        if (snakeHead.atPosition(snakeObject.x, snakeObject.y) && snakeHead != snakeObject) {
          snakeHead.setDead(true);
          setLost(true);
        }
      }
    }

    int length = gameObjects.size();
    for (int i = 0; i < length; i++) {
      if (gameObjects.get(i) instanceof Apple) {
        if (((Apple) gameObjects.get(i)).isEaten()) {
          deleteItem(gameObjects.get(i));
          i--;
          length--;
        }
      }
    }

    if (eatApple) {
      addSnakeComponent();
    }

    Random random = new Random();
    int randomInt = random.nextInt(100);
    if (randomInt == 50){
      Apple apple = new Apple(random.nextInt(gridHeight - 2) + 1,
              random.nextInt(gridHeight - 2) + 1, size, shape);
      addSnakeObj(apple);
      apple.setColor(Color.RED);
    }

    randomInt = random.nextInt(500);
    if (randomInt == 100) {
      addSnakeComponent();
    }

    distance++;
  }

  void turnSnake(TurnDirection turnDirection) {
    snakeHead.turn(turnDirection);
  }

  private void eatApple(Apple apple) {
    apple.setIsEaten(true);
    this.apples += 1;
  }

  private void deleteItem(GameObject g) {
    gameObjects.remove(g);
  }

  private void addSnakeComponent() {
    SnakeComponent component = snakeHead.addComponent();
    addSnakeObj(component);
    component.setColor(Color.GREEN);
    snakeLength++;
  }

  void createObjects() {
    Random random = new Random();

    for (int x = 0; x < gridWidth; x++) {
      gameObjects.add(new Wall(x, 0, size, shape));
      gameObjects.add(new Wall(x, gridHeight - 1, size, shape));
    }
    for (int y = 0; y < gridHeight; y++) {
      gameObjects.add(new Wall(0, y, size, shape));
      gameObjects.add(new Wall(gridWidth - 1, y, size, shape));
    }
    addSnakeObj(new Apple(random.nextInt(gridHeight - 2) + 1,
            random.nextInt(gridHeight - 2) + 1, size, shape));
    addSnakeObj(new Apple(random.nextInt(gridHeight - 2) + 1,
            random.nextInt(gridHeight - 2) + 1, size, shape));
    addSnakeObj(new Apple(random.nextInt(gridHeight - 2) + 1,
            random.nextInt(gridHeight - 2) + 1, size, shape));

    snakeHead = new SnakeHead(gridWidth / 2, gridHeight / 2, size, shape);
    gameObjects.add(snakeHead);

    addSnakeComponent();
    addSnakeComponent();

    setAppleColor(Color.RED);
    setBodyColor(Color.GREEN);
    setHeadColor(Color.YELLOW);
    setWallColor(Color.YELLOW);
  }

  public void setShape(SnakeShape shape){
      this.shape = shape;
      for (GameObject gameObject: gameObjects) {
          ((SnakeObject) gameObject).setShape(shape);
      }
  }

  public void drawBackground(Canvas canvas){
    Paint paint = new Paint();
    paint.setColor(canvasColor);
    paint.setStyle(Paint.Style.FILL);
    canvas.drawPaint(paint);
  }

  public void setCanvasColor(int color) {
    canvasColor = color;
  }

  @Override
  public boolean isGameOver() {
    return lost;
  }

  @Override
  public int getCurrentScore() {
    return this.snakeLength;
  }

  public String getStatistics(){
    return this.snakeLength + "," + this.apples + "," + this.distance + "," + this.lost;
  }

  @Override
  @NonNull
  public String toString(){
      StringBuilder string = new StringBuilder();
      for (GameObject gameObject: gameObjects){
          if (gameObject instanceof SnakeObject && !(gameObject instanceof Wall)){
              SnakeObject snakeObject = (SnakeObject) gameObject;
              string.append(snakeObject.toString() + ",");
          }
      }
      return string.toString();
  }
}
