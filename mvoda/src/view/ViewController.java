package view;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.management.modelmbean.XMLParseException;

import lombok.Getter;
import lombok.Setter;
import playlist.Playlist;
import playlist.PlaylistEntry;
import themes.Theme;
import util.ThemeFinder;
import util.ThemeFinderImpl;
import view.buttons.Dialog;

public class ViewController implements Initializable {

	//TODO: you've given these fx:id's but why? you don't do anything with the button but onClick...., so why do they need to be here
	//public Button loadPlaylistButton;


	@Getter @Setter ViewControllerListener viewListener;	
	@Getter @Setter Stage stage;
	@FXML @Getter @Setter ComboBox<Theme> themeSelectBox;
	@FXML @Getter @Setter ListView<PlaylistEntry> playlistView;
	@FXML TextArea mediaInfoArea;

	public Button savePlaylistButton;
	public TextField trackTextField;
	public TextField artistTextField;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {		
		playlistView.setDisable(true); //we will enable the playlistview when it populates with items
		savePlaylistButton.disableProperty().bind(playlistView.disabledProperty()); //the save playlist button will follow the playlistview buttons enable status

		initThemeSelectBox(); //reads themes 

		//custom cell factory for the playlist entries
		playlistView.setCellFactory(new Callback<ListView<PlaylistEntry>, ListCell<PlaylistEntry>>() {
			@Override public ListCell<PlaylistEntry> call(ListView<PlaylistEntry> list) {
				return new PlaylistEntryListCell();
			}
		});

		//tie the track and artist and media select boxes to the currently selected playlist entry
		playlistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlaylistEntry>() {
			public void changed( final ObservableValue<? extends PlaylistEntry> ov, PlaylistEntry old_val, PlaylistEntry new_val ) {
				if (ov == null || ov.getValue() == null) { return; } // TODO: remove this
				SimpleStringProperty sspTrack = new SimpleStringProperty( ov.getValue().getTrackName() );
				SimpleStringProperty sspArtist = new SimpleStringProperty(ov.getValue().getArtistName() );

				sspTrack.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ovTrack, String old_val, String new_val) { 
						ov.getValue().setTrackName(ovTrack.getValue().toString());
					}
				});

				sspArtist.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ovArtist, String old_val, String new_val) { 
						ov.getValue().setArtistName(ovArtist.getValue().toString());
					}
				});		

				trackTextField.textProperty().bindBidirectional(sspTrack);
				artistTextField.textProperty().bindBidirectional(sspArtist); 

				if (ov == null || ov.getValue().getVideo() == null) {
					mediaInfoArea.setText("File Not Found");
				}
				else {
					mediaInfoArea.setText(ov.getValue().getVideo().toString());//write media info to screen for this entry
				}
			}
		});
	}

	/**
	 * Initialises the combobox that holds themes - these are held directly as Themes in the box and their toString() methods provide the text in the box
	 */
	public void initThemeSelectBox() {
		ThemeFinder themeFinder = new ThemeFinderImpl(); //we must instantiate the themeFinder because it implements an interface - could use a factory instead
		ArrayList<Theme> themeTemp = new ArrayList<>(); //we don't want the themeFinder to be tied to JavaFX's observable list imp
		try { themeTemp = themeFinder.returnThemes(); } 
		catch (IOException e) {e.printStackTrace();}  // TODO exception handling 	
		catch (InterruptedException e) { e.printStackTrace(); } // TODO exception handling
		themeSelectBox.setItems(FXCollections.observableList(themeTemp) ); //NOW we make an observable list from our array list when we set it as the box's list
	}

	public void sendPlaylistNodesToScreen(Playlist playlist) {
		for (PlaylistEntry playlistEntry : playlist.getPlaylistEntries())
			playlistView.getItems().add(playlistEntry);
	}

	@FXML void loadPlaylist(ActionEvent e) throws InterruptedException {
		try { viewListener.loadPlaylist(); 
		playlistView.setDisable(false); //TODO only disable these if it goes well.....
		} 
		catch (NullPointerException e1) { popup(e1.getMessage()); }
		catch (XMLParseException e2) { popup("Error: not a valid MV-CoDA XML file"); }	
		catch (FileNotFoundException e3) { popup(e3.getMessage() ); }
		catch (IOException e4) { popup("Error: Could not close the input file"); }
		catch (MediaOpenException e5) { popup(e5.getMessage()); }
	}

	@FXML void savePlaylist(ActionEvent e) { 
		try { viewListener.savePlaylist(); } 
		catch (NullPointerException e1) { popup(e1.getMessage()); }
		catch (FileNotFoundException e2) { popup("Error: Could not access the ouptut file"); }
		catch (IOException e3) { popup("Error: Could not close the ouptut file"); }
	}

	@FXML void newPlaylist(ActionEvent e) { viewListener.newPlaylist(); themeSelectBox.getSelectionModel().clearSelection(); }

	@FXML void addPlaylistEntry(ActionEvent e) throws IOException { //TOD: loading a music video exception please
		try {
			PlaylistEntry entry = viewListener.addPlaylistEntry();

		}
		catch (MediaOpenException e5) { popup(e5.getMessage()); }
		playlistView.setDisable(false);
	}

	@FXML void deletePlaylistEntry(ActionEvent e) {
		viewListener.deletePlaylistEntry();
		//if (playlistObservable.isEmpty()) { playlistView.setDisable(true); }
	}

	@FXML void moveUp(ActionEvent e) {
		//MoveButtons moveUpButton = new MoveButtons(playlistView);
		//moveUpButton.moveUp(e);	
		viewListener.moveUp();
	}

	@FXML void moveDown(ActionEvent e) {
		//Dialog dialog = new Dialog();
		//Dialog.dialogBox(stage, "ehat");//, new Scene());
		try {
			viewListener.moveDown();
		} catch (IndexOutOfBoundsException error) {
			// TODO Auto-generated catch block
			//Dialog.dialogBox(stage, "No playlist loaded. Please first create or load a playlist");//, new Scene());
			error.printStackTrace();
		}
	}

	@FXML void render(ActionEvent e) {
		try {
			viewListener.render();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (XMLParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void popup(String text) {
		Dialog.dialogBox(stage, text);
	}


	public static FileChooser getFileChooser(String filetype) {
		final FileChooser fileChooser = new FileChooser();
		//below we receive a full filetype i.e: .mp4 and convert to the word MP4 for the filetype notification
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(filetype.substring(1).toUpperCase() + " files (*" + filetype + ")", "*" + filetype);
		fileChooser.getExtensionFilters().add(extFilter);
		return fileChooser;
	}

}




