# SIMPLE TEXT EDITOR
Project is written in Java and is functionality and appearance wise similar to Windows Notepad, among other things text insertion, deletion, selection, dynamic resizing of text in the app window etc. It is built using JavaFX GUI library and following an MVC design pattern. <br />
Other design patterns used in this project are e.g. command pattern - undo/redo options and observer pattern - selection of letters (each letter changes background when selected).

![Demo](/src/app/View/demo.gif/)

<b>Textfield implementation (time/space complexity - TC/SC):</b> <br />
• Each letter (node) is saved in a sequence into a linked list and accessed through a corresponding HashMap
for constant insert/delete TC O(1). Same TC O(1) of access goes for clicking on letters using mouse and moving cursor on the line.
Moving between lines (up and down) is TC O(n), n = number of letters in a line.<br /> 
• Rendering of text happens in TC O(n) with each change to the text nodes in a linked list and/or resizing of window (together with the text content inside the pane).

• HashMap and linked list both have SC of O(n), n = number of elements. 
