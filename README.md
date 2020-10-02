# SIMPLE TEXT EDITOR
Simple text editor written in Java (similar to Windows Notepad) with a couple custom adjustments 
(e.g. text dynamically resizes with the window). Built from scratch using JavaFX GUI library and following an MVC design pattern.<br /> 

![Demo](/src/app/View/demo.gif/)

<b>Textfield implementation (time/space complexity - TC/SC):</b> <br />
• Each letter (node) is saved in a sequence into a linked list and accessed through a corresponding HashMap
for constant insert/delete TC O(1). Same TC O(1) of access goes for clicking on letters using mouse and moving cursor on the line.
Moving between lines (up and down) is TC O(n), n = number of letters in a line.<br /> 
• Rendering of text happens in TC O(n) with each change to the text nodes in a linked list and/or resizing of window (together with the text content inside the pane).

• HashMap and linked list both have SP of O(n), n = number of elements. 
