(function() {
  "use strict";

  var pointer = JSON8Pointer;

  var sections = [
    {
      id: "target",
      value: {
        firstName: "John",
        lastName: "Smith",
        age: 25,
        address: {
          streetAddress: "21 2nd Street",
          city: "New York",
          state: "NY",
          postalCode: "10021",
        },
        phoneNumber: [
          {
            type: "home",
            number: "212 555-1234",
          },
          {
            type: "fax",
            number: "646 555-4567",
          },
        ],
        gender: {
          type: "male",
        },
      },
    },
    {
      id: "flatten",
    },
    {
      id: "unflatten",
    },
  ];

  var editors = {};

  document.addEventListener("DOMContentLoaded", function() {
    sections.forEach(function(section) {
      var input = (section.textarea = document.querySelector(
        "#" + section.id + " textarea"
      ));

      if (section.value) {
        input.value = JSON.stringify(section.value, null, 2);
      }

      editors[section.id] = section.editor = CodeMirror.fromTextArea(
        section.textarea,
        {
          lineNumbers: true,
          mode: section.value ? "application/json" : "text/plain",
          gutters: ["CodeMirror-lint-markers", "CodeMirror-foldgutter"],
          lint: true,
          matchBrackets: true,
          autoCloseBrackets: true,
          foldGutter: true,
          readOnly: section.id === "result",
        }
      );
    });

    function run() {
      var target;

      try {
        target = JSON.parse(editors["target"].getValue());
      } catch (e) {
        ["flatten", "unflatten"].forEach(function(id) {
          editors[id].setOption("mode", "text/plain");
          editors[id].setValue("target is not valid JSON\n\n" + e);
        });
        return;
      }

      editors["flatten"].setOption("mode", "application/json");
      editors["flatten"].setValue(
        JSON.stringify(pointer.flatten(target), null, 2)
      );

      editors["unflatten"].setOption("mode", "application/json");
      editors["unflatten"].setValue(
        JSON.stringify(
          pointer.unflatten(JSON.parse(editors["flatten"].getValue())),
          null,
          2
        )
      );
    }

    [editors["target"]].forEach(function(editor) {
      editor.on("change", function() {
        run();
      });
    });

    run();
  });
})();
