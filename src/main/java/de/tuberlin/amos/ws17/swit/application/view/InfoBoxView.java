package de.tuberlin.amos.ws17.swit.application.view;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;

public class InfoBoxView extends BorderPane {

    private BorderPane titlePane;
    private BorderPane contentPane;
    private Button     closeButton;
    private Text       title;
    private Label      information;
    private ImageView  image;
    private ScrollPane scrollPane;
    private RingProgressView indicator = new RingProgressView();


    private static Font fontTitle = new Font(12);
    private static Font fontText  = new Font(12);

    public InfoBoxView() {
        setupViews();
    }

    private void setupViews() {
        title = new Text();
        title.setId("expansionName");

        title.setFont(fontTitle);
        title.setStyle("" +
                "-fx-fill: white;" +
                "-fx-effect: dropshadow( gaussian , black , 8, 0.90 , 0 , 0 );");
        //title.set
        BorderPane.setAlignment(title, Pos.CENTER_LEFT);
        BorderPane.setMargin(title, new Insets(4));

        Text text = new Text();
        text.setText("X");
        text.setFont(new Font(30));
        text.setStyle("" +
                "-fx-fill: white;" +
                "-fx-effect: dropshadow( gaussian , black , 8, 0.90 , 0 , 0 );");

        //Button b = new Button(null, );
        closeButton = new Button(null, text);
        closeButton.setId("expansionButton");
        closeButton.setMinHeight(64);
        closeButton.setMinWidth(64);
        //closeButton.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        titlePane = new BorderPane();
        titlePane.setId("expansionTopPane");
        titlePane.setLeft(title);
        titlePane.setRight(closeButton);
        BorderPane.setAlignment(closeButton, Pos.CENTER_LEFT);
        titlePane.setStyle("" +
                "-fx-background-color: rgba(255, 255, 255, 0.25); " +
                "-fx-padding: 0 0 0 8");

        image = new ImageView();
        image.setId("expansionImage");
        BorderPane.setAlignment(image, Pos.CENTER);
        image.setPreserveRatio(true);
        image.setFitHeight(150);
        image.minHeight(0);
        image.minWidth(0);

        information = new Label();
        information.setId("expansionInformation");
        information.setAlignment(Pos.TOP_CENTER);
        information.setFont(fontText);
        //information.setPrefHeight(300);
        information.setStyle("-fx-fill: white;" +
                "-fx-effect: dropshadow( gaussian , black , 4, 0.90 , 0 , 0 );");

        contentPane = new BorderPane();
        contentPane.setId("expansionContentPane");
        contentPane.setTop(image);
        contentPane.setBottom(information);

        scrollPane = new ScrollPane();
        scrollPane.setId("expansionScrollPane");
        scrollPane.setContent(contentPane);
        scrollPane.setFitToWidth(true);


        setId("expansionPane");
        setVisible(false);
        BorderPane.setAlignment(this, Pos.CENTER);
        setTop(titlePane);
        setCenter(image);
        setBottom(scrollPane);
        setRight(indicator);

        Screen screen = Screen.getPrimary();
        Rectangle2D screenVisualBounds = screen.getVisualBounds();
        setMaxWidth(screenVisualBounds.getWidth() * 0.3);
        setMaxHeight(screenVisualBounds.getHeight() * 0.6);
        scrollPane.setMaxHeight(screenVisualBounds.getHeight() * 0.3);
    }


    public void setOnScrollListener(EventHandler<ScrollEvent> eventHandler) {
        contentPane.setOnScroll(eventHandler);
    }

    public Button getCloseButton() {
        return closeButton;
    }

    public Text getTitle() {
        return title;
    }

    public Label getInformation() {
        return information;
    }

    public ImageView getImage() {
        return image;
    }

    public ScrollPane getScrollPane() {
        return scrollPane;
    }

    public RingProgressView getIndicator() {
        return indicator;
    }
}
