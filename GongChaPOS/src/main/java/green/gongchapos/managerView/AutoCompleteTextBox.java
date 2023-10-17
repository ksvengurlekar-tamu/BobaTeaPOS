package green.gongchapos.managerView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;


/** Class for the AutoCompleteTextBox used for auto-complete used in Inventory View within the Manager View.
 *
 * @author Camila Brigueda, Rose Chakraborty, Eyad Nazir, Jedidiah Samrajkumar, Kiran Vengurlekar
 */
public class AutoCompleteTextBox extends TextField
{
  /** The existing autocomplete entries. */
  private final SortedSet<String> entries;

  /** The popup used to select an entry. */
  private ContextMenu entriesPopup;


  /** AutoCompleteTextBox is a custom text box class with autocomplete functionality.
   *
   */
  public AutoCompleteTextBox() {
    super();
    entries = new TreeSet<>();
    entriesPopup = new ContextMenu();
    textProperty().addListener(new ChangeListener<String>()
    {
      @Override
      /** This method is called when the text in the autocomplete textfield changes.
       *
       * @param observableValue The textbox that waits for user input
       * @param s the
       */
      public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
        if (getText().length() == 0) {
          entriesPopup.hide();
        } else {
          LinkedList<String> searchResult = new LinkedList<>();
          searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
          if (entries.size() > 0) {
            populatePopup(searchResult);
            if (!entriesPopup.isShowing()) {
              entriesPopup.show(AutoCompleteTextBox.this, Side.BOTTOM, 0, 0);
            }
          } else {
            entriesPopup.hide();
          }
        }
      }
    });

    focusedProperty().addListener(new ChangeListener<Boolean>() {
      @Override
      /** Handles changes in the ObservableValue for a Boolean property in the AutoCompleteTextBox.
      *
      * @param observableValue The ObservableValue represents a boolean property.
      * @param aBoolean The previous value of the Boolean property.
      * @param aBoolean2 The new value of the Boolean property.
      */
      public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
        entriesPopup.hide();
      }
    });
  }


  /** Get the existing set of autocomplete entries.
   *
   * @return The existing autocomplete entries.
   */
  public SortedSet<String> getEntries() { return entries; }


  /** Populate the entry set with the given search results. Display is limited to 10 entries, for performance.
   *
   * @param searchResult The set of matching strings.
   */
  private void populatePopup(List<String> searchResult) {
    List<CustomMenuItem> menuItems = new LinkedList<>();
    // If you'd like more entries, modify this line.
    int maxEntries = 10;
    int count = Math.min(searchResult.size(), maxEntries);
    for (int i = 0; i < count; i++)
    {
      final String result = searchResult.get(i);
      Label entryLabel = new Label(result);
      CustomMenuItem item = new CustomMenuItem(entryLabel, true);
      item.setOnAction(new EventHandler<ActionEvent>()
      {
        @Override
        /** Handles an ActionEvent triggered by a user action, such as selecting an item from a popup menu.
        *
        * @param actionEvent The ActionEvent triggered by the user action.
        */
        public void handle(ActionEvent actionEvent) {
          setText(result);
          entriesPopup.hide();
        }
      });
      menuItems.add(item);
    }
    entriesPopup.getItems().clear();
    entriesPopup.getItems().addAll(menuItems);
  }
}
