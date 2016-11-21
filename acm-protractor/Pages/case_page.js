var logger = require('../log');
var waitHelper = require('../util/waitHelper.js');
var util = require('../util/utils.js');
var Objects = require('../json/Objects.json');
var taskPage = require('../Pages/task_page.js');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var newBtn = element(by.linkText(Objects.casepage.locators.newButton));
var newCaseBtn = element(by.linkText(Objects.casepage.locators.newCaseBtn));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseArson = element(by.linkText(Objects.casepage.locators.caseArson));
var casesTitle = element(by.xpath(Objects.casepage.locators.casesTitle));
var casesType = element(by.xpath(Objects.casepage.locators.casesType));
var casesPageTitle = element(by.xpath(Objects.casepage.locators.casesPageTitle));
var caseTitle = element(by.name(Objects.casepage.locators.caseTitle));
var caseTypeDropDown = element(by.className(Objects.casepage.locators.caseType));
var nextBtn = element(by.xpath(Objects.casepage.locators.nextBtn));
var firstName = element(by.name(Objects.casepage.locators.firstName));
var lastName = element(by.name(Objects.casepage.locators.lastName));
var submitBtn = element(by.xpath(Objects.casepage.locators.submitBtn));
var changeCaseStatusBtn = element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn));
//var newBtn = element(by.xpath(Objects.casepage.locators.newCasesBtn));
var editBtn = element(by.xpath(Objects.casepage.locators.editBtn));
var subscribe = element(by.xpath(Objects.casepage.locators.subscribeBtn));
var mergeBtn = element(by.xpath(Objects.casepage.locators.mergeBtn));
var splitBtn = element(by.xpath(Objects.casepage.locators.splitBtn));
var changeCaseStatusTitle = element(by.className(Objects.casepage.locators.changeCaseStatusTitle));
var changeStatusDropDown = element(by.className(Objects.casepage.locators.changeStatusDropDown));
var statusClosed = element(by.xpath(Objects.casepage.locators.statusClosed));
var selectApprover = element(by.xpath(Objects.casepage.locators.selectApprover));
var searchForUser = element(by.xpath(Objects.casepage.locators.searchForUser));
var goBtn = element(by.xpath(Objects.casepage.locators.goBtn));
var addBtn = element(by.xpath(Objects.casepage.locators.addBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUser));
var tasksLinkBtn = element(by.xpath(Objects.casepage.locators.tasksLink));
var refreshBtn = element(by.xpath(Objects.casepage.locators.refreshBtn));
var taskTitle = element(by.xpath(Objects.casepage.locators.taskTitle));
var priorityLink = element(by.xpath(Objects.casepage.locators.priority));
var priorityDropDownEdit = element(by.xpath(Objects.casepage.locators.priorityDropDown));
var priorityBtn = element(by.xpath(Objects.casepage.locators.priorityBtn));
var createdDate = element(by.xpath(Objects.casepage.locators.createdDate));
var assigneeLink = element(by.xpath(Objects.casepage.locators.assignee));
var assigneeDropDown = element(by.xpath(Objects.casepage.locators.assigneeDropDown));
var assigneeBtn = element(by.xpath(Objects.casepage.locators.assigneeBtn));
var expandLinksButton = element(by.xpath(Objects.casepage.locators.expandLinksButton));
var notesLink = element(by.xpath(Objects.casepage.locators.notesLink));
var addNoteBtn = element(by.xpath(Objects.casepage.locators.addNoteBtn));
var noteTextArea = element(by.model(Objects.casepage.locators.noteTextArea));
var saveNoteBtn = element(by.xpath(Objects.casepage.locators.saveNoteBtn));
var addedNoteName = element.all(by.repeater(Objects.casepage.locators.addedNoteName)).get(0);
var deleteNoteBtn = element.all(by.repeater(Objects.casepage.locators.deleteNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editNoteBtn = element.all(by.repeater(Objects.casepage.locators.editNoteBtn)).get(3).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var emptyNoteTable = element(by.xpath(Objects.casepage.locators.emptyNoteTable));
var showLinksBtn = element(by.xpath(Objects.casepage.locatorsshowLinksBtn));
var addNewTaskBtn = element(by.xpath(Objects.casepage.locators.addNewTaskBtn));
var taskAssighnee = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(1);
var taskCreated = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(2);
var taskPriority = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(3);
var taskDueDate = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(4);
var taskStatus = element.all(by.repeater(Objects.casepage.locators.taskTableRows)).get(5);
var poeopleLinkBtn = element(by.xpath(Objects.casepage.locators.peopleLinkBtn));
var peopleTypeColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1);
var peopleFirstNameColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(2);
var peopleLastNameColumn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(3);
var peopleTable = element(by.xpath(Objects.casepage.locators.peopleTable));
var addPeopleBtn = element(by.xpath(Objects.casepage.locators.addPeopleBtn));
var personsTypeDropDown = element(by.model(Objects.casepage.locators.personsTypeDropDown));
var personFirstNameInput = element(by.model(Objects.casepage.locators.personFirstNameInput));
var personLastNameInput = element(by.model(Objects.casepage.locators.personLastNameInput));
var savePersonBtn = element(by.xpath(Objects.casepage.locators.savePersonBtn));
var peopleTypeColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var peopleFirstNameColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var peopleLastNameColumnSecondRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var deletePersonBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var editPersonBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var addContactMethodBtn = element(by.css(Objects.casepage.locators.addContactMethodBtn));
var contactMethodsLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var contactMethodTypes = element(by.model(Objects.casepage.locators.contactMethodTypes));
var contactValue = element(by.model(Objects.casepage.locators.contactValue));
var saveContactBtn = element(by.css(Objects.casepage.locators.saveContactBtn));
var contactMethodTypeFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var contactMethodValueFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var contactMethodLastModifiedFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var contactMethodModifiedByFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var contactMethodDeleteBtn = element(by.css(Objects.casepage.locators.deleteContactMethodBtn));
var contactMethodEditBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(9).all(by.tagName(Objects.casepage.locators.tag)).get(0);
var emptyContactMethodTable = element(by.css(Objects.casepage.locators.emptyContactMethodTable));
var editContactMethodBtn = element(by.css(Objects.casepage.locators.editContactMethodBtn));
var organizationsLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(1);
var addOrganizationBtn = element(by.css(Objects.casepage.locators.addOgranizationBtn));
var organizationTypeDropdown = element(by.model(Objects.casepage.locators.organizationTypeDropDown));
var organizationValueInput = element(by.model(Objects.casepage.locators.organizationValueInput));
var saveOrganizationBtn = element(by.css(Objects.casepage.locators.saveOrganizationBtn));
var organizationTypeFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var organizationValueFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var organizationLastModifiedFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var organizationModifiedByFirstRow = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var organizationDeleteBtn = element(by.css(Objects.casepage.locators.organizationDeleteBtn));
var organizationEditBtn = element(by.css(Objects.casepage.locators.organizationEditBtn));
var addressLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(2);
var addAddressBtn = element(by.css(Objects.casepage.locators.addAddressBtn));
var addressTypeDropDow = element(by.model(Objects.casepage.locators.addressTypeDropDow));
var streetAddress = element(by.model(Objects.casepage.locators.streetAddress));
var addressCity = element(by.model(Objects.casepage.locators.addressCity));
var addressState = element(by.model(Objects.casepage.locators.addressState));
var addressZip = element(by.model(Objects.casepage.locators.addressZip));
var addressCountry = element(by.model(Objects.casepage.locators.addressCountry));
var saveAddressBtn = element(by.css(Objects.casepage.locators.saveAddressBtn));
var addressTypeValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var addressStreetValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var addressCityValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var addressStateValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var addressZipValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(9);
var addressCountryValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(10);
var addresLastModifiedByValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(11);
var addressModifiedByValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(12);
var deleteAddressBtn = element(by.css(Objects.casepage.locators.deleteAddressBtn));
var editAddressBtn = element(by.css(Objects.casepage.locators.editAddressBtn));
var aliassesLinkBtn = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(0).all(by.tagName(Objects.casepage.locators.tag)).get(3);
var addAliasesBtn = element(by.css(Objects.casepage.locators.addAliasesBtn));
var aliasesDropdown = element(by.model(Objects.casepage.locators.aliasesDropdown));
var aliasesInputValue = element(by.model(Objects.casepage.locators.aliasesValue));
var saveAliasBtn = element(by.css(Objects.casepage.locators.saveAliasesBtn));
var aliasesTypeValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5);
var aliasesValue = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6);
var aliasesLastModified = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(7);
var aliasesModifiedBy = element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(8);
var deleteAliasBtn = element(by.css(Objects.casepage.locators.deleteAliasBtn));
var editAliasBtn = element(by.css(Objects.casepage.locators.editAliasBtn));


var CasePage = function() {

    browser.ignoreSynchronization = true;
    this.navigateToNewCasePage = function(){
        newCaseBtn.click();
        return this;
    };
    this.submitGeneralInformation = function(title, type) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.caseTitle))), 30000);
        caseTitle.click().then(function () {
            caseTitle.sendKeys(title);
        });

        browser.wait(EC.visibilityOf(element(by.className(Objects.casepage.locators.caseType))), 30000);
        caseTypeDropDown.click().then(function () {

            var caseType = element(by.linkText(type));
            waitHelper.waitElementToBeVisible(caseType);
            caseType.click();
        });

        nextBtn.click();
        return this;
    };

    this.initiatorInformation = function(firstname, lastname) {

        browser.wait(EC.visibilityOf(element(by.name(Objects.casepage.locators.firstName))), 30000);
        firstName.click().then(function () {
            firstName.sendKeys(firstname);
        });
        lastName.click().then(function () {
            lastName.sendKeys(lastname);
        });

        submitBtn.click();
        return this;
    };


    this.waitForCaseType = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesType))), 30000);

    };

    this.waitForCaseTitle = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.casesTitle))), 30000);

    };

    this.returnCasesPageTitle = function() {

        return casesPageTitle.getText();

    };

    this.returnCaseTitle = function() {

            return casesTitle.getText();
    };


    this.returnCaseType = function() {


        return casesType.getText();

    };

    this.waitForChangeCaseButton = function() {

            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn))), 30000);

        };

    this.clickRefreshBtn = function() {

        refreshBtn.click();

    };
    this.clickChangeCaseBtn = function() {

        changeCaseStatusBtn.click().then(function() {
            browser.ignoreSynchronization = true;

        });

        return this;

    };
    this.selectCaseStatusClosed = function() {

        browser.ignoreSynchronization = true;
        browser.wait(EC.visibilityOf(element(by.className(Objects.casepage.locators.changeCaseStatusTitle))), 15000);
        changeStatusDropDown.click().then(function() {
            browser.wait(EC.textToBePresentInElement((statusClosed), Objects.casepage.data.statusClosed), 10000).then(function() {



                statusClosed.click();
            });
        });

        return this;
    };
    this.selectApprover = function(approverSamuel) {

        selectApprover.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addUser))), 10000);
            searchForUser.click();
            searchForUser.sendKeys(approverSamuel);
            goBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUser))), 3000);
                searchedUser.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addBtn))), 3000);
                    addBtn.click();
                });
            });
        });
        return this;
    }

    this.chnageCaseSubmit = function() {
        browser.executeScript('arguments[0].click()', submitBtn);
    };

    this.returnAutomatedTask = function() {

        return taskTitle.getText();

    }

    this.clickTaskTitle = function() {
        taskTitle.click();
    };

    this.waitForPriority = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.priority))), 20000);
    };

    this.returnPriority = function() {
        return priorityLink.getText();
    };

    this.waitForCreatedDate = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.createdDate))), 20000);
    };

    this.returnCreatedDate = function() {
        return createdDate.getText();
    };

    this.editPriority = function(priority) {

        priorityLink.click().then(function() {
            priorityDropDownEdit.$('[value="string:' + priority + '"]').click().then(function() {
                priorityBtn.click();
            });
        });
        return this;
    };

    this.editAssignee = function(assignee) {

        assigneeLink.click().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath("//*[@class='clearfix']/div[3]/div[1]/div/form/div/select/option[8]"))), 5000).then(function() {
                assigneeDropDown.$('[value="string:' + assignee + '"]').click().then(function() {
                    assigneeBtn.click();
                });

            });
        });
        return this;
    }

    this.waitForAssignee = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.casepage.locators.assignee))), 20000);
    };

    this.returnAssignee = function() {
        return assigneeLink.getText();
    };

    this.clickExpandLinks = function(){
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.changeCaseStatusBtn))), 30000);
        expandLinksButton.click();
        return this;
    };

    this.clickAddTaskButton = function() {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.addNewTaskBtn))), 30000).then(function() {
            addNewTaskBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.id(Objects.taskpage.locators.subject))), 30000);
            });
        });

        return this;
    }

    this.clickPeopleLinkBtn = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.peopleLinkBtn))), 30000).then(function() {
            poeopleLinkBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.peopleTable))), 15000).then(function() {
                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1)), 15000);
                });
            });
        });
        return this;
    }
    this.returnPeopleType = function() {
        return peopleTypeColumn.getText();
    }

    this.returnPeopleFirstName = function() {
        return peopleFirstNameColumn.getText();
    }
    this.returnPeopleLastName = function() {
        return peopleLastNameColumn.getText();
    }

    this.addPerson = function(personType, firstName, lastName) {

        addPeopleBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.personsTypeDropDown))), 15000, "Add people pop up is not shown").then(function() {
                personsTypeDropDown.$('[value="string:' + personType + '"]').click().then(function() {
                    personFirstNameInput.click().then(function() {
                        personFirstNameInput.sendKeys(firstName).then(function() {
                            personLastNameInput.click().then(function() {
                                personLastNameInput.sendKeys(lastName).then(function() {
                                    savePersonBtn.click().then(function() {
                                        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 15000, "Person is not added");
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }


    this.returnPeopleTypeSecondRow = function() {
        return peopleTypeColumnSecondRow.getText();
    }
    this.returnPeopleFirstNameColumnSecondRow = function() {
        return peopleFirstNameColumnSecondRow.getText();
    }

    this.returnPeopleLastNameColumnSecondRow = function() {
        return peopleLastNameColumnSecondRow.getText();
    }

    this.deletePerson = function() {
        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(1))).then(function() {
            deletePersonBtn.click().then(function() {
                element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                    expect(items.length).toBe(0, "The person is not deleted");
                });

            });
        });
        return this;
    }
    this.editPerson = function(personType, firstName, lastName) {
        browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(4).all(by.tagName(Objects.casepage.locators.tag)).get(0))).then(function() {
            editPersonBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.personsTypeDropDown))), 15000, "Add people pop up is not shown").then(function() {
                    personsTypeDropDown.$('[value="string:' + personType + '"]').click().then(function() {
                        personFirstNameInput.clear().then(function() {
                            personFirstNameInput.sendKeys(firstName).then(function() {
                                personLastNameInput.clear().then(function() {
                                    personLastNameInput.sendKeys(lastName).then(function() {
                                        savePersonBtn.click().then(function() {
                                            browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(1)), 15000);
                                        });
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });

        return this;
    }

    this.addContactMethod = function(contactMethodType, contactvalue) {

        contactMethodsLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addContactMethodBtn))), 10000).then(function() {
                addContactMethodBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.contactMethodTypes))), 10000).then(function() {
                        contactMethodTypes.$('[value="string:' + contactMethodType + '"]').click().then(function() {
                            contactValue.sendKeys(contactvalue).then(function() {
                                saveContactBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5)), 10000).then(function() {
                                        browser.sleep(8000);
                                        contactMethodsLinkBtn.click();
                                    });
                                });
                            });
                        });

                    });
                });

            });
        });
        return this;

    }

    this.returnContatMethodType = function() {
        return contactMethodTypeFirstRow.getText();

    }

    this.returncontactMethodValueFirstRow = function() {

        return contactMethodValueFirstRow.getText();
    }

    this.returncontactMethodLastModifiedFirstRow = function() {
        return contactMethodLastModifiedFirstRow.getText();
    }

    this.returncontactMethodModifiedByFirstRow = function() {
        return contactMethodModifiedByFirstRow.getText();
    }

    this.deleteContactMethod = function() {
        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.deleteContactMethodBtn))), 10000).then(function() {
            contactMethodDeleteBtn.click().then(function() {
                element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                    expect(items.length).toBe(5, "The contact method is not deleted");
                });
            });
        });
        return this;
    }

    this.editContactMethod = function(contactMethodType, contactvalue) {

        browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.editContactMethodBtn))), 10000).then(function() {
            editContactMethodBtn.click().then(function() {
                browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.contactMethodTypes))), 10000).then(function() {
                    contactMethodTypes.$('[value="string:' + contactMethodType + '"]').click().then(function() {
                        contactValue.clear().then(function() {
                            contactValue.sendKeys(contactvalue).then(function() {
                                saveContactBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(5)), 10000);
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.addOrganization = function(organizationType, organizationvalue) {

        organizationsLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addOgranizationBtn))), 10000).then(function() {
                addOrganizationBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.organizationTypeDropDown))), 10000).then(function() {
                        organizationTypeDropdown.$('[value="string:' + organizationType + '"]').click().then(function() {
                            organizationValueInput.sendKeys(organizationvalue).then(function() {
                                saveOrganizationBtn.click().then(function() {
                                    browser.sleep(8000);
                                    organizationsLinkBtn.click();
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    }

    this.returnorganizationTypeFirstRow = function() {
        return organizationTypeFirstRow.getText();
    }

    this.returnorganizationValueFirstRow = function() {
        return organizationValueFirstRow.getText();
    }

    this.returnorganizationLastModifiedFirstRow = function() {
        return organizationLastModifiedFirstRow.getText();
    }

    this.returnorganizationModifiedByFirstRow = function() {
        return organizationModifiedByFirstRow.getText();
    }

    this.deleteOrganization = function() {
        organizationDeleteBtn.click().then(function() {
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The organization is not deleted");
            });
        });
        return this;
    }

    this.editOrganization = function(organizationType, organizationvalue) {
        organizationEditBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.organizationTypeDropDown))), 10000).then(function() {
                organizationTypeDropdown.$('[value="string:' + organizationType + '"]').click().then(function() {
                    organizationValueInput.clear().then(function() {
                        organizationValueInput.sendKeys(organizationvalue).then(function() {
                            saveOrganizationBtn.click().then(function() {
                                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000);
                            });
                        });
                    });
                });
            });

        });

        return this;
    }

    this.addAddress = function(addressType, street, city, state, zip, country) {

        addressLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addAddressBtn))), 10000).then(function() {
                addAddressBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addressTypeDropDow))), 10000).then(function() {
                        addressTypeDropDow.$('[value="string:' + addressType + '"]').click().then(function() {
                            streetAddress.sendKeys(street).then(function() {
                                addressCity.sendKeys(city).then(function() {
                                    addressState.sendKeys(state).then(function() {
                                        addressZip.sendKeys(zip).then(function() {
                                            addressCountry.sendKeys(country).then(function() {
                                                saveAddressBtn.click().then(function() {
                                                    browser.sleep(8000);
                                                    addressLinkBtn.click();
                                                });
                                            });

                                        });

                                    });

                                });

                            });

                        });
                    });

                });

            });

        });


        return this;
    }
    this.returnAddressType = function() {
        return addressTypeValue.getText();
    }
    this.returnAddressStreet = function() {
        return addressStreetValue.getText();
    }
    this.returnAddressCity = function() {
        return addressCityValue.getText();
    }
    this.returnAddressState = function() {
        return addressStateValue.getText();
    }
    this.returnAddressZip = function() {
        return addressZipValue.getText();
    }

    this.returnaddressCountryValue = function() {
        return addressCountryValue.getText();
    }
    this.returnAddressModifiedBy = function() {
        return addressModifiedByValue.getText();
    }
    this.returnAddressLastModified = function() {
        return addresLastModifiedByValue.getText();
    }

    this.returnAddressModifiedBy = function() {
        return addressModifiedByValue.getText();
    }

    this.deleteAddress = function() {
        deleteAddressBtn.click().then(function() {
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The address is not deleted");
            });
        });
        return this;
    }

    this.editAddress = function(addressType, street, city, state, zip, country) {

        editAddressBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.addressTypeDropDow))), 10000).then(function() {
                addressTypeDropDow.$('[value="string:' + addressType + '"]').click().then(function() {
                    streetAddress.clear().then(function() {
                        streetAddress.sendKeys(street).then(function() {
                            addressCity.clear().then(function() {
                                addressCity.sendKeys(city).then(function() {
                                    addressState.clear().then(function() {
                                        addressState.sendKeys(state).then(function() {
                                            addressZip.clear().then(function() {
                                                addressZip.sendKeys(zip).then(function() {
                                                    addressCountry.clear().then(function() {
                                                        addressCountry.sendKeys(country).then(function() {
                                                            saveAddressBtn.click();
                                                        });
                                                    });
                                                });

                                            });
                                        });
                                    });

                                });
                            });
                        });
                    });
                });
            });

        });
    }
    this.returnAliasesType = function() {
        return aliasesTypeValue.getText();
    }
    this.returnAliasesValue = function() {
        return aliasesValue.getText();
    }
    this.returnAliasesLastModified = function() {
        return aliasesLastModified.getText();
    }
    this.returnAliasesModifiedBy = function() {
        return aliasesModifiedBy.getText();
    }

    this.addAlias = function(aliasType, aliasValue) {

        aliassesLinkBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.css(Objects.casepage.locators.addAliasesBtn))), 10000).then(function() {
                addAliasesBtn.click().then(function() {
                    browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.aliasesDropdown))), 10000).then(function() {
                        aliasesDropdown.$('[value="string:' + aliasType + '"]').click().then(function() {
                            aliasesInputValue.sendKeys(aliasValue).then(function() {
                                saveAliasBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000).then(function() {
                                        browser.sleep(8000);
                                        aliassesLinkBtn.click();

                                    });
                                });

                            });

                        });

                    });

                });
            });
        });
        return this;

    }

    this.deleteAlias = function() {
        deleteAliasBtn.click().then(function() {
            element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).then(function(items) {
                expect(items.length).toBe(5, "The alias is not deleted");
            });
        });
        return this;
    }

    this.editAlias = function(aliasType, aliasValue) {
        editAliasBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.casepage.locators.aliasesDropdown))), 10000).then(function() {
                aliasesDropdown.$('[value="string:' + aliasType + '"]').click().then(function() {
                    aliasesInputValue.clear().then(function() {
                        aliasesInputValue.sendKeys(aliasValue).then(function() {
                            saveAliasBtn.click().then(function() {
                                browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.casepage.locators.peopleTableColumns)).get(6)), 10000);

                            });

                        });
                    });
                });
            });
        });

        return this;
    }
};


CasePage.prototype = basePage;
module.exports = new CasePage();

