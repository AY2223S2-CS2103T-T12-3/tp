---
layout: page
title: Developer Guide
---
* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

* {list here sources of all reused/adapted ideas, code, documentation, and third-party libraries -- include links to the original source as well}
* --------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

<div markdown="span" class="alert alert-primary">

:bulb: **Tip:** The `.puml` files used to create diagrams in this document can be found in the [diagrams](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/docs/diagrams/) folder. Refer to the [_PlantUML Tutorial_ at se-edu/guides](https://se-education.org/guides/tutorials/plantUml.html) to learn how to create and edit diagrams.
</div>

### Architecture

<img src="images/ArchitectureDiagram.png" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** has two classes called [`Main`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/Main.java) and [`MainApp`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/MainApp.java). It is responsible for,
* At app launch: Initializes the components in the correct sequence, and connects them up with each other.
* At shut down: Shuts down the components and invokes cleanup methods where necessary.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

The rest of the App consists of four components.

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.


**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `deletePerson 1`.

<img src="images/ArchitectureSequenceDiagram.png" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<img src="images/ComponentManagers.png" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/ui/Ui.java)

![Structure of the UI Component](images/UiClassDiagram.png)

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `BookListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` or `Book` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<img src="images/LogicClassDiagram.png" width="550"/>

How the `Logic` component works:
1. When `Logic` is called upon to execute a command, it uses the `ExpressLibraryParser` class to parse the user command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `AddCommand`) which is executed by the `LogicManager`.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `AddPersonCommand`) which is executed by the `LogicManager`.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

The Sequence Diagram below illustrates the interactions within the `Logic` component for the `execute("deletePerson 1")` API call.

![Interactions Inside the Logic Component for the `deletePerson 1` Command](images/DeleteSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.
</div>

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<img src="images/ParserClasses.png" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `ExpressLibraryParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddPersonCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddPersonCommand`) which the `ExpressLibraryParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddPersonCommandParser`, `DeletePersonCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/model/Model.java)

<img src="images/ModelClassDiagram.png" width="450" />


The `Model` component,

* stores the ExpressLibrary data i.e., all `Person` objects (which are contained in a `UniquePersonList` object) and all `Book` objects (which are contained in a `UniqueBookList`).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change. Similar process for `Book` objects as well.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<div markdown="span" class="alert alert-info">

:information_source: **Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `ExpressLibrary`, which `Person` references. This allows `ExpressLibrary` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<img src="images/BetterModelClassDiagram.png" width="450" />

</div>


### Storage component

**API** : [`Storage.java`](https://github.com/AY2223S2-CS2103T-T12-3/tp/tree/master/src/main/java/expresslibrary/storage/Storage.java)

<img src="images/StorageClassDiagram.png" width="550" />

The `Storage` component,
* can save both express library data and user preference data in json format, and read them back into corresponding objects.
* inherits from both `ExpressLibraryStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `expresslibrary.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Borrow/return feature

#### Implementation

The borrow feature will allow the user to mark a book as borrowed by a person.
Similarly, the return feature will allow the user to mark a book as returned to the ExpressLibrary by a person.

It implements the following commands:

* `borrow PERSON_INDEX b/BOOK_INDEX d/DUE_DATE`
* `return PERSON_INDEX b/BOOK_INDEX`

Given below is an example usage scenario of the borrow command:

The user executes `borrow 1 b/2 d/23/11/2023` command to lend the 2nd Book in the Book list to the 1st Person in the Person list.

The `BorrowCommand` class will first retrieve the Person object and Book object from the given indexes and create a copy of them (since they are immutable) called `editedPerson` and `bookToBorrow`.

Then, it calls `borrowBook` on `editedPerson` and passes in the Book object `bookToBorrow`. This will add the Book to the Person's `Set` of books field. Similarly, the class also calls `loanBookTo` on `bookToBorrow` to update the Book's fields with the borrower, the borrow date (current date) and the due date details. 

Given below is an example usage scenario of the return command:

The user executes `return 2 b/1` command to return the 1st book in the Book list from the 2nd person in the Person list.

The `ReturnCommand` class will first retrieve the Person object and Book object from the given indexes and create a copy of them (since they are immutable) called `editedPerson` and `bookToReturn`.

Then, it calls `returnBook` on `editedPerson` and passes in the Book object `bookToReturn`. This will remove the Book from the Person's `Set` of books field. Similarly, the class also calls `returnBook` on `bookToReturn` to update and remove the Person from the Book's borrower field as well as clear the borrow and due dates.

### \[Proposed\] Undo/redo feature

#### Proposed Implementation

The proposed undo/redo mechanism is facilitated by `VersionedExpressLibrary`. It extends `ExpressLibrary` with an undo/redo history, stored internally as an `ExpressLibraryStateList` and `currentStatePointer`. Additionally, it implements the following operations:

* `VersionedExpressLibrary#commit()` — Saves the current express library state in its history.
* `VersionedExpressLibrary#undo()` — Restores the previous express library state from its history.
* `VersionedExpressLibrary#redo()` — Restores a previously undone express library state from its history.

These operations are exposed in the `Model` interface as `Model#commitExpressLibrary()`, `Model#undoExpressLibrary()` and `Model#redoExpressLibrary()` respectively.

Given below is an example usage scenario and how the undo/redo mechanism behaves at each step.

Step 1. The user launches the application for the first time. The `VersionedExpressLibrary` will be initialized with the initial express library state, and the `currentStatePointer` pointing to that single express library state.

![UndoRedoState0](images/UndoRedoState0.png)

Step 2. The user executes `delete 5` command to delete the 5th person in the express library. The `delete` command calls `Model#commitExpressLibrary()`, causing the modified state of the express library after the `delete 5` command executes to be saved in the `expressLibraryStateList`, and the `currentStatePointer` is shifted to the newly inserted express library state.

![UndoRedoState1](images/UndoRedoState1.png)

Step 3. The user executes `add n/David …​` to add a new person. The `add` command also calls `Model#commitExpressLibrary()`, causing another modified express library state to be saved into the `expressLibraryStateList`.

![UndoRedoState2](images/UndoRedoState2.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If a command fails its execution, it will not call `Model#commitExpressLibrary()`, so the express library state will not be saved into the `expressLibraryStateList`.

</div>

Step 4. The user now decides that adding the person was a mistake, and decides to undo that action by executing the `undo` command. The `undo` command will call `Model#undoExpressLibrary()`, which will shift the `currentStatePointer` once to the left, pointing it to the previous express library state, and restores the express library to that state.

![UndoRedoState3](images/UndoRedoState3.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index 0, pointing to the initial ExpressLibrary state, then there are no previous ExpressLibrary states to restore. The `undo` command uses `Model#canUndoExpressLibrary()` to check if this is the case. If so, it will return an error to the user rather
than attempting to perform the undo.

</div>

The following sequence diagram shows how the undo operation works:

![UndoSequenceDiagram](images/UndoSequenceDiagram.png)

<div markdown="span" class="alert alert-info">:information_source: **Note:** The lifeline for `UndoCommand` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline reaches the end of diagram.

</div>

The `redo` command does the opposite — it calls `Model#redoExpressLibrary()`, which shifts the `currentStatePointer` once to the right, pointing to the previously undone state, and restores the express library to that state.

<div markdown="span" class="alert alert-info">:information_source: **Note:** If the `currentStatePointer` is at index `expressLibraryStateList.size() - 1`, pointing to the latest express library state, then there are no undone ExpressLibrary states to restore. The `redo` command uses `Model#canRedoExpressLibrary()` to check if this is the case. If so, it will return an error to the user rather than attempting to perform the redo.

</div>

Step 5. The user then decides to execute the command `list`. Commands that do not modify the express library, such as `list`, will usually not call `Model#commitExpressLibrary()`, `Model#undoExpressLibrary()` or `Model#redoExpressLibrary()`. Thus, the `expressLibraryStateList` remains unchanged.

![UndoRedoState4](images/UndoRedoState4.png)

Step 6. The user executes `clear`, which calls `Model#commitExpressLibrary()`. Since the `currentStatePointer` is not pointing at the end of the `expressLibraryStateList`, all express library states after the `currentStatePointer` will be purged. Reason: It no longer makes sense to redo the `add n/David …​` command. This is the behavior that most modern desktop applications follow.

![UndoRedoState5](images/UndoRedoState5.png)

The following activity diagram summarizes what happens when a user executes a new command:

<img src="images/CommitActivityDiagram.png" width="250" />

#### Design considerations:

**Aspect: How undo & redo executes:**

* **Alternative 1 (current choice):** Saves the entire express library.
  * Pros: Easy to implement.
  * Cons: May have performance issues in terms of memory usage.

* **Alternative 2:** Individual command knows how to undo/redo by
  itself.
  * Pros: Will use less memory (e.g. for `delete`, just save the person being deleted).
  * Cons: We must ensure that the implementation of each individual command are correct.

_{more aspects and alternatives to be added}_

### \[Proposed\] Data archiving

_{Explain here how the data archiving feature will be implemented}_


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* has a need to manage a significant number of books and library users
* prefer desktop apps over other types
* can type fast
* prefers typing to mouse interactions
* is reasonably comfortable using CLI apps

**Value proposition**: manage users and books faster than a typical mouse/GUI driven app


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​       | I want to …​                                      | So that I can…​                                                    |
|----------|---------------|---------------------------------------------------|--------------------------------------------------------------------|
| `* * *`  | librarian          | add a new book that has not been added in the app | track the status of the new book                                   
| `* * *`  | librarian          | edit the details of a specific book               | track any changes to the book                         |
| `* * *`  | librarian           | delete a book                                     | not track it if the book is no longer in the library                            |                     
| `* * *`  | librarian          | have the data automatically saved in hard drive    | refer to the data in the future                                    |
| `* *`    | librarian           | be able to keep a record of all the people who have borrowed books | better manage the library                                          |
| `* *`    | librarian           | add the book to the contact details of the person who borrowed them | rent the book out while keeping track of the book and the due date |
| `* *`    | librarian           | remove a book from a patron if the patron has returned the book | allow the book to be borrowed by other patrons                    |
| `*`      | advanced librarian  | edit the data file                                | adjust the data to suit my needs                                   |


*{More to be added}*

### Use cases

(For all use cases below, the **System** is the `ExpressLibrary` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Delete a person**

**MSS**

1. User requests to list persons
2. ExpressLibrary shows a list of persons
3. User requests to delete a specific person in the list
4. ExpressLibrary deletes the person

    Use case ends.

**Use case: Add book borrowed to a person**

**MSS**

1.  User requests to list persons
2.  ExpressLibrary shows a list of persons
3.  User requests to add a book to a specific person in the list
4.  ExpressLibrary adds a book field to the person

    Use case ends.

**Extensions**

* 2a. The list is empty.

  Use case ends.

* 3a. The given index is invalid.

    * 3a1. ExpressLibrary shows an error message.

      Use case resumes at step 2.

*{More to be added}*

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  Should be able to load the application within 2 seconds.

*{More to be added}*

### Glossary

* **Mainstream OS**: Windows, Linux, Unix, OS-X
* **Book Status**: Status of the book, whether the book is available to borrow or not

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<div markdown="span" class="alert alert-info">:information_source: **Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</div>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

1. _{ more test cases …​ }_

### Deleting a person

1. Deleting a person while all persons are being shown

   1. Prerequisites: List all persons using the `listPerson` command. Multiple persons in the list.

   1. Test case: `deletePerson 1`<br>
      Expected: First contact is deleted from the list. Details of the deleted contact shown in the status message. Timestamp in the status bar is updated.

   1. Test case: `deletePerson 0`<br>
      Expected: No person is deleted. Error details shown in the status message. Status bar remains the same.

   1. Other incorrect delete commands to try: `deletePerson`, `deletePerson x`, `...` (where x is larger than the list size)<br>
      Expected: Similar to previous.

1. _{ more test cases …​ }_

### Saving data

1. Dealing with missing/corrupted data files

   1. _{explain how to simulate a missing/corrupted file, and the expected behavior}_

1. _{ more test cases …​ }_
