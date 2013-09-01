package view;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import media.Encoder;
import media.MusicVideo;
import media.xuggle.DecodeAndPlayAudioAndVideo;
import media.xuggle.EncoderXuggle;
import media.xuggle.MusicVideoXuggle;
import playlist.Playlist;
import playlist.PlaylistEntry;
import themes.Theme;
import themes.ThemeFinder;
import themes.ThemeFinderImpl;
import themes.XMLReader;
import themes.XMLSerialisable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Callback;

public class ViewController implements Initializable {

	//TODO: you've given these fx:id's but why? you don't do anything with the button but onClick...., so why do they need to be here
	//public Button loadPlaylistButton;
	//public Button renderButton;
	//public Button addPlaylistEntryButton;

	public TextField trackTextField;
	public TextField artistTextField;

	private ObservableList<PlaylistEntry> playlistObservable = FXCollections.observableArrayList(new ArrayList<PlaylistEntry>());

	public Playlist playlist = new Playlist("Biggest Beats I've seen in a while"); //TODO: playlist name

	public ArrayList<String> vidFiles = new ArrayList<>();

	private Desktop desktop = Desktop.getDesktop();

	@Getter @Setter ViewControllerListener viewListener;

	//@FXML ObservableList<String> themeSelectBox; 
	@FXML ComboBox themeSelectBox;

	@FXML ListView<PlaylistEntry> playlistView;

	public void sendPlaylistNodesToScreen(Playlist videos) {
		playlistView.setCellFactory(new Callback<ListView<PlaylistEntry>, ListCell<PlaylistEntry>>() {
			@Override public ListCell<PlaylistEntry> call(ListView<PlaylistEntry> list) {
				return new PlaylistEntryListCell();
			}
		});

		for (PlaylistEntry playlistEntry : videos.getPlaylistEntries())
			playlistView.getItems().add(playlistEntry);
	}


	@FXML void loadPlaylist(ActionEvent e) {		
		final FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			try { 
				desktop.open(file); 
			} 
			catch (IOException ex) { System.out.println("oops cant open file"); }
		}

	}

	@FXML void playlistEntryEntered(ActionEvent e) {
		String name = ""; // get from textbox
		viewListener.onNewTrackAvailable(name);
	}

	@FXML void savePlaylist(ActionEvent e) {
		final FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(null);
		if(file != null){ 
			try {
				FileWriter fileWriter = null;
				fileWriter = new FileWriter(file);
				fileWriter.write("some random text");
				fileWriter.close();
			} catch (IOException ex) { System.out.println("error saving the file"); } }
	}

	@FXML void newPlaylist(ActionEvent e) { //(MouseEvent e) {
		makeAPlaylist();
	}




	@FXML void render(ActionEvent e) {
		playlist.resetArray(playlistObservable);

		for (int i=0;i < playlist.getPlaylistEntries().size();i++) {
			System.out.println("At postion: " + (i + 1) + " We have " + playlist.getPlaylistEntries().get(i).getVideo().getFileUNC() );
		}



		//mock the theme
		String themeName = "Classic";
		Path rootDir = Paths.get("Theme");
		Path themeDir = Paths.get(rootDir.toString(),themeName);
		XMLSerialisable themeAsSerialisable = XMLReader.readXML(themeDir, themeName);
		Theme theme = (Theme) themeAsSerialisable;
		Path properDir = Paths.get( Theme.getRootDir().toString(), theme.getItemName() );

//and do it
		
		//draw onto video
		

		//TODO: first we must ask where you want to save with a dialog
		final FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MP4 files (*.MP4)", "*.MP4");
		fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showSaveDialog(null);
		String outFileUNC = file.toString();
		System.out.println("Here's what I'm going to send you: " + file);
		if(file != null){ 
				//mock an output file
				Encoder draw = new EncoderXuggle(playlist, theme, outFileUNC);
		}//} catch (IOException ex) { System.out.println("error saving the file"); } }//TODOthe encoder is the thing writing....
		
		
		
		
		DecodeAndPlayAudioAndVideo player = new DecodeAndPlayAudioAndVideo(outFileUNC);
		/*//make a couple of music vid paths
		String fileUNC = "../../../MVODAInputs/BrunoShort.mp4";
		String fileUNC2 = "../../../MVODAInputs/FlorenceShort.mp4";
		String fileUNC3 = "../../../MVODAInputs/GloriaShort.mp4";
		String fileUNC4 = "../../../MVODAInputs/KateShort.mp4";
		String fileUNC5 = "../../../MVODAInputs/LeonaShort.mp4";
		String fileUNC6 = "../../../MVODAInputs/MaroonShort.mp4";
		String fileUNC7 = "../../../MVODAInputs/NeyoShort.mp4";
		String fileUNC8 = "../../../MVODAInputs/NickiShort.mp4";
		String fileUNC9 = "../../../MVODAInputs/PinkShort.mp4";
		String fileUNC10 = "../../../MVODAInputs/RihannaShort.mp4";

		//make vids out of them
		MusicVideo test = new MusicVideoXuggle(fileUNC);
		MusicVideo test2 = new MusicVideoXuggle(fileUNC2);
		MusicVideo test3 = new MusicVideoXuggle(fileUNC3);
		MusicVideo test4 = new MusicVideoXuggle(fileUNC4);
		MusicVideo test5 = new MusicVideoXuggle(fileUNC5);
		MusicVideo test6 = new MusicVideoXuggle(fileUNC6);
		MusicVideo test7 = new MusicVideoXuggle(fileUNC7);
		MusicVideo test8 = new MusicVideoXuggle(fileUNC8);
		MusicVideo test9 = new MusicVideoXuggle(fileUNC9);
		MusicVideo test10 = new MusicVideoXuggle(fileUNC10);


		//make a couple of playlist entries
		PlaylistEntry playlistEntry = new PlaylistEntry(test);
		PlaylistEntry playlistEntry2 = new PlaylistEntry(test2);
		PlaylistEntry playlistEntry3 = new PlaylistEntry(test3);
		PlaylistEntry playlistEntry4 = new PlaylistEntry(test4);
		PlaylistEntry playlistEntry5 = new PlaylistEntry(test5);
		PlaylistEntry playlistEntry6 = new PlaylistEntry(test6);
		PlaylistEntry playlistEntry7 = new PlaylistEntry(test7);
		PlaylistEntry playlistEntry8 = new PlaylistEntry(test8);
		PlaylistEntry playlistEntry9 = new PlaylistEntry(test9);
		PlaylistEntry playlistEntry10 = new PlaylistEntry(test10);


		//make a playlist
		Playlist playlist = new Playlist("Biggest Beats I've seen in a while");
		playlist.setNextEntry(playlistEntry);
		playlist.setNextEntry(playlistEntry2);
		playlist.setNextEntry(playlistEntry3);
		playlist.setNextEntry(playlistEntry4);
		playlist.setNextEntry(playlistEntry5);
		playlist.setNextEntry(playlistEntry6);
		playlist.setNextEntry(playlistEntry7);
		playlist.setNextEntry(playlistEntry8);
		playlist.setNextEntry(playlistEntry9);
		playlist.setNextEntry(playlistEntry10);

		//set an output file
		String outFileUNC = "E:/Output.mp4";

		//Pop.setNum(1); //TODO: very silly AND has to be done before instantiation...

		String themeName = "Classic";
		Path rootDir = Paths.get("Theme");
		Path themeDir = Paths.get(rootDir.toString(),themeName);

		XMLSerialisable themeAsSerialisable = XMLReader.readXML(themeDir, themeName);
		Theme theme = (Theme) themeAsSerialisable;

		Path properDir = Paths.get( Theme.getRootDir().toString(), theme.getItemName() );

		System.out.println("This is the dir: " + theme.getThemeDir());
		System.out.println("This is the root dir: " + Theme.getRootDir());
		System.out.println("and this is the logo: " + theme.getLogo());
		System.out.println("AND THE REAL PATH IS:" + properDir);

		//get Xuggler's video info - idea could Junit test compare MY music vid class to THIS info?
		System.out.println(test.toString());
		//draw onto video
		Encoder draw = new EncoderXuggle(playlist, theme, outFileUNC);
		test.close();
		DecodeAndPlayAudioAndVideo player = new DecodeAndPlayAudioAndVideo(outFileUNC);*/
	}



	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		//themeSelectBox.add("Hello");
		ThemeFinder themeFinder = new ThemeFinderImpl();
		ArrayList<Theme> themes = new ArrayList<>();
		try {
			themes = themeFinder.returnThemes();
		} catch (IOException e) { // TODO exception handling 
			e.printStackTrace();

		} catch (InterruptedException e) { // TODO exception handling
			e.printStackTrace();
		}
		ObservableList<String> themename = themeSelectBox.getItems(); //TODO: how to instantiate this without having to do that....
		themename.clear();
		for (Theme element : themes) {
			String name = element.getItemName();
			themename.add(name);
		}

		//themeSelectBox.setAll(themename);
		//ObservableList<String> list = themeSelectBox.getItems();
		System.out.println(themename);
		themeSelectBox.setItems(themename);


		//makeAPlaylist();

		playlistView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<PlaylistEntry>() {
			public void changed(final ObservableValue<? extends PlaylistEntry> ov, 
					PlaylistEntry old_val, PlaylistEntry new_val) {

				// TODO: remove this
				if (ov == null || ov.getValue() == null)
					return;

				//trackTextField.textProperty().bindBidirectional(new StringBeanProperty(ov.getValue(), "artistName"));

				SimpleStringProperty sspTrack = new SimpleStringProperty(ov.getValue().getTrackName());
				SimpleStringProperty sspArtist = new SimpleStringProperty(ov.getValue().getArtistName());

				sspTrack.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ovTrack, 
							String old_val, String new_val) { 
						ov.getValue().setTrackName(ovTrack.getValue().toString());
					}
				});

				sspArtist.addListener(new ChangeListener<String>() {
					public void changed(ObservableValue<? extends String> ovArtist, 
							String old_val, String new_val) { 
						ov.getValue().setArtistName(ovArtist.getValue().toString());
					}
				});		

				trackTextField.textProperty().bindBidirectional(sspTrack);
				artistTextField.textProperty().bindBidirectional(sspArtist); 

				/*            	playlistObservable.addListener(new ListChangeListener<PlaylistEntry>() {

            		@Override
            		public void onChanged(
            				javafx.collections.ListChangeListener.Change<? extends PlaylistEntry> c) {
            			//videos.resetArray(playlistObservable);
            			//for (int j = 0; j < playlistObservable.size(); j++) {
            			//	playlistObservable.get(j).setPositionInPlaylist(j + 1);
            			//	videos.getPlaylistEntries().get(j).setPositionInPlaylist(j + 1);	
            			}
            			//sendPlaylistNodesToScreen(videos);
            			//forceListRefreshOn(playlistView);

            			//playlistView.getSelectionModel().clearAndSelect(indexOfItemToMove - 1);	
            			//playlistView.getFocusModel().focus(indexOfItemToMove - 1);	

            		}
            	});*/

			}
		});
	}

	/**
	 * Makes an array of file unc paths to ten videos, then makes a new playlist, turns the UNC's into playlist entry music vids and adds them to the playlist,
	 */
	public void makeAPlaylist() {



		vidFiles.add("../../../MVODAInputs/BrunoShort.mp4");
		vidFiles.add("../../../MVODAInputs/FlorenceShort.mp4");
		vidFiles.add("../../../MVODAInputs/GloriaShort.mp4");
		vidFiles.add("../../../MVODAInputs/KateShort.mp4");
		vidFiles.add("../../../MVODAInputs/LeonaShort.mp4");
		vidFiles.add("../../../MVODAInputs/MaroonShort.mp4");
		vidFiles.add("../../../MVODAInputs/NeyoShort.mp4");
		vidFiles.add("../../../MVODAInputs/NickiShort.mp4");
		vidFiles.add("../../../MVODAInputs/PinkShort.mp4");
		vidFiles.add("../../../MVODAInputs/RihannaShort.mp4");





		for (int i = 0; i < vidFiles.size(); i++) {
			PlaylistEntry entry = new PlaylistEntry( new MusicVideoXuggle( vidFiles.get( i ) ),"Track" + (i + 1) , "Artist" + (i + 1 ) );
			entry.setPositionInPlaylist(i + 1); //set the playlist entry number while we have a loop! may be a problem later.....
			playlist.setNextEntry(entry);
		}

		/*videos.setNextEntry(new PlaylistEntry(new MusicVideoXuggle(fileUNC), "Track 1", "Artist 1"));
				videos.setNextEntry(new PlaylistEntry(new MusicVideoXuggle(fileUNC2), "Track 2", "Artist 2"));
		 */
		sendPlaylistNodesToScreen(playlist);

		playlistObservable = playlistView.getItems();
	}

	public void addPlaylistEntry(ActionEvent e) throws IOException { //TOD: loading a music video exception please
		final FileChooser fileChooser = new FileChooser();
		//FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
		//fileChooser.getExtensionFilters().add(extFilter);
		File file = fileChooser.showOpenDialog(null);
		if (file != null) {
			String fileUNC = file.getAbsolutePath();
			MusicVideo vid = new MusicVideoXuggle(fileUNC);
			PlaylistEntry entry = new PlaylistEntry( vid, "Track" + (playlist.getSize() + 1), "Artist" + (playlist.getSize() + 1) );
			entry.setPositionInPlaylist(playlist.getSize() + 1);//no point in doing this really
			playlist.setNextEntry(entry);
			ObservableList<PlaylistEntry> playlistObservable = playlistView.getItems();
			playlistObservable.add(entry);
			//sendPlaylistNodesToScreen(videos);	

		}

	}


	public void deletePlaylistEntry(ActionEvent e) { //https://gist.github.com/jewelsea/5559262
		PlaylistEntry toDelete = playlistView.getSelectionModel().getSelectedItem();
		int indexOfItemToDelete = playlistView.getSelectionModel().getSelectedIndex();
		//ObservableList<PlaylistEntry> playlistObservable = playlistView.getItems();
		playlistView.getItems().remove(indexOfItemToDelete);
	}


	public void moveUp(ActionEvent e) {
		int indexOfItemToMove = playlistView.getSelectionModel().getSelectedIndex();

		if (indexOfItemToMove < 0) return; //don't attempt to move the top item

		PlaylistEntry temp = playlistView.getSelectionModel().getSelectedItem(); //temp entry holds the entry we want to move
		playlistView.getItems().set(indexOfItemToMove, playlistView.getItems().get(indexOfItemToMove - 1)); //set replaces the item: so move item below to selected index
		playlistView.getItems().set(indexOfItemToMove - 1, temp); //now move 

		PlaylistEntry movingUp = playlistView.getSelectionModel().getSelectedItem();

		PlaylistEntry movingDown = playlistView.getItems().get(indexOfItemToMove - 1);

		movingUp.setPositionInPlaylist(indexOfItemToMove);
		movingDown.setPositionInPlaylist(indexOfItemToMove + 1);

		System.out.println("Moving Up: " + movingUp.getPositionInPlaylist() + "; " + movingUp.getVideo().getFileUNC());
		System.out.println("Moving Down: " + movingDown.getPositionInPlaylist() + "; " + movingDown.getVideo().getFileUNC());

		/*	forceListRefreshOn(playlistView);

	playlistView.getSelectionModel().clearAndSelect(indexOfItemToMove - 1);	
	playlistView.getFocusModel().focus(indexOfItemToMove - 1);	
	playlistView.requestLayout();
		 */	

	}

	public void moveDown(ActionEvent e) {
		int indexOfItemToMove = playlistView.getSelectionModel().getSelectedIndex();
		int lastIndex = playlistView.getItems().size() -1;

		if (indexOfItemToMove > lastIndex) return; //TODO: still causing exception is it because i'm only catching the viewbox's error conditiion not the lists?

		PlaylistEntry temp = playlistView.getSelectionModel().getSelectedItem();
		playlistView.getItems().set(indexOfItemToMove, playlistView.getItems().get(indexOfItemToMove + 1));
		playlistView.getItems().set(indexOfItemToMove + 1, temp);

		PlaylistEntry movingDown = playlistView.getSelectionModel().getSelectedItem();

		PlaylistEntry movingUp = playlistView.getItems().get(indexOfItemToMove + 1);

		movingDown.setPositionInPlaylist(indexOfItemToMove);
		movingUp.setPositionInPlaylist(indexOfItemToMove);

		System.out.println("Moving Down: " + movingDown.getPositionInPlaylist() + "; " + movingDown.getVideo().getFileUNC());
		System.out.println("Moving Up: " + movingUp.getPositionInPlaylist() + "; " + movingUp.getVideo().getFileUNC());
	}



	// adapted from: http://stackoverflow.com/questions/16880115/javafx-2-2-how-to-force-a-redraw-update-of-a-listview
	private void forceListRefreshOn(ListView lsv) {
		ObservableList items = lsv.getItems();
		lsv.setItems(lsv.getItems());
		lsv.setItems(items);
	}



}
