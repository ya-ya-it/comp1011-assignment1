/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import models.FictionBook;

/**
 * FXML Controller class
 *
 * @author Dasha
 */
public class AddNewBookViewController implements Initializable {

    private ObservableList<FictionBook> books;
    private FileChooser fileChooser;
    private File imageFile;
    
    @FXML private TextField titleField;
    @FXML private TextField authorNameField;
    @FXML private ComboBox fictionGenreComboBox;
    @FXML private TextField mainCharacterField;
    @FXML private DatePicker dateOfPublicationDatePicker;
    @FXML private TextField priceField;
    @FXML private TextField amountInStockField;
    @FXML private TextField amountSoldField;
    @FXML private ImageView bookImage;
    @FXML private Label errorMsg;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorMsg.setVisible(false);
        
        try
        {
            BufferedImage bufferedImage = ImageIO.read(new File("./src/images/placeholder-cover.png"));
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            bookImage.setImage(image);
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        
        fictionGenreComboBox.getItems().addAll(FictionBook.FictionGenre.values());
        fictionGenreComboBox.setVisibleRowCount(3);
        fictionGenreComboBox.setEditable(false);
        fictionGenreComboBox.setPromptText("Choose Genre");
        
    }
    
    public void initialData(ObservableList<FictionBook> listOfBooks) {
        books = listOfBooks;
    }
    
    public void saveBookButtonPushed(ActionEvent event) throws IOException {
        try
        {
            String title = titleField.getText();
            String author = authorNameField.getText();
            FictionBook.FictionGenre genre;
            LocalDate dateOfPublication;
            String mainCharacters = mainCharacterField.getText();
            BigDecimal price = new BigDecimal(priceField.getText());
            int amountInStock;
            int amountSold;
            Image bookCover = bookImage.getImage();
            
            if (fictionGenreComboBox.getSelectionModel().isEmpty()) {
                throw new IllegalArgumentException("Please choose genre");
            } else {
                genre = (FictionBook.FictionGenre)fictionGenreComboBox.getSelectionModel().getSelectedItem();
            }
            
            if (dateOfPublicationDatePicker.getValue() == null) {
                throw new IllegalArgumentException("Please choose a date of publication");
            } else {
                dateOfPublication = dateOfPublicationDatePicker.getValue();
            }
            
            if(!amountInStockField.getText().matches("[0-9]")) {
                throw new IllegalArgumentException("Please enter the book amount in stock");
            } else {
                amountInStock = Integer.parseInt(amountInStockField.getText());
            }
            
            if(!amountSoldField.getText().matches("[0-9]")) {
                throw new IllegalArgumentException("Please enter the correct amount of sold books");
            } else {
                amountSold = Integer.parseInt(amountSoldField.getText());
            }
            
            FictionBook newBook = new FictionBook(title, author, genre, mainCharacters,
                                        price, dateOfPublication, amountInStock,
                                        amountSold,bookCover);
            books.add(newBook);
            
            changeScene(event, "FictionBookshelfView.fxml");
        }
        catch (IllegalArgumentException e)
        {
            errorMsg.setVisible(true);
            errorMsg.setText(e.getMessage());
        }
    }
    
    public void changeScene(ActionEvent event, String fxmlFileName) throws IOException
    {
        //load a new scene
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(fxmlFileName));
        Parent parent = loader.load();
        Scene newScene = new Scene(parent);
        
        //access the controller of the new Scene and send over
        //the current list of employees
        FictionBookshelfViewController controller = loader.getController();
        controller.loadBooks(books);
        
        //Get the current "stage" (aka window) 
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        //change the scene to the new scene
        stage.setTitle("Bookshelf");
        stage.setScene(newScene);
        stage.show();
    }
    
    public void backButtonPushed(ActionEvent event) throws IOException {
        changeScene(event, "FictionBookshelfView.fxml");
    }
    
    public void chooseImageButtonPushed(ActionEvent event) {
                //get the stage to open a new window
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        
        fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image");
        
        //filter for only .jpg and .png files
        FileChooser.ExtensionFilter jpgFilter 
                = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter pngFilter 
                = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        
        fileChooser.getExtensionFilters().addAll(jpgFilter, pngFilter);
        
        
        //Set to the user's picture directory or C drive if not available
        String userDirectoryString = System.getProperty("user.home");
        File userDirectory = new File(userDirectoryString);
        
        if (!userDirectory.canRead())
            userDirectory = new File("c:/");
        
        fileChooser.setInitialDirectory(userDirectory);
        
        //open the file dialog window
        imageFile = fileChooser.showOpenDialog(stage);
        
        //ensure the user selected a file
        if (imageFile.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(imageFile);
                Image image = SwingFXUtils.toFXImage(bufferedImage,null);
                bookImage.setImage(image);
            }
            catch (IOException e)
            {
                System.err.println(e.getMessage());
            }
        }

    }
}
