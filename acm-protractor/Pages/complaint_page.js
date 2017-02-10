var Objects = require('../json/Objects.json');
var basePage = require('./base_page.js');
var EC = protractor.ExpectedConditions;
var SelectWrapper = require('../util/select-wrapper.js');
var waitHelper = require('../util/waitHelper.js');
var complaintButton = element(By.linkText(Objects.complaintPage.locators.complaintButton));
var firstName = element(By.name(Objects.complaintPage.locators.firstName));
var lastName = element(By.name(Objects.complaintPage.locators.lastName));
var initiatorTab = element(By.xpath(Objects.complaintPage.locators.initiatorTab));
var incidentTab = element(By.xpath(Objects.complaintPage.locators.incidentTab));
var peopleTab = element(By.xpath(Objects.complaintPage.locators.peopleTab));
var attachmentsTab = element(By.xpath(Objects.complaintPage.locators.attachmentsTab));
var participantsTab = element(By.xpath(Objects.complaintPage.locators.participantsTab));
var incidentCategoryDDListBox = element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox));
var complaintTitle = element(By.name(Objects.complaintPage.locators.complaintTitle));
var submitButton = element(By.xpath(Objects.complaintPage.locators.submitButton));
var nextButton = element(by.xpath(Objects.casepage.locators.nextBtn));
var radioButtonNewInitiator = element(by.xpath(Objects.complaintPage.locators.radioButtonNewInitiator));
var closeComplaintButton = element(by.xpath(Objects.complaintPage.locators.closeComplaintButton));
var complaintDispositionDDListBox = element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox));
var closeComplaintDescription = element(by.css(Objects.complaintPage.locators.closeComplaintDescription));
var selectApprover = element(by.name(Objects.casepage.locators.selectApprover));
var searchForUser = element(by.css(Objects.casepage.locators.searchField));
var goBtn = element(by.xpath(Objects.casepage.locators.goBtn));
var addBtn = element(by.xpath(Objects.casepage.locators.addBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUser));
var complaintID = element(by.xpath(Objects.casepage.locators.caseID));
var complaintType = element(by.xpath(Objects.complaintPage.locators.complaintType));
var complaintPriority = element(by.xpath(Objects.complaintPage.locators.complaintPriority));
var complaintCreateDate = element(by.xpath(Objects.casepage.locators.createdDate));
var complaintTitleSaved = element(by.xpath(Objects.complaintPage.locators.complaintTitleLink));
var newBtn = element(by.xpath(Objects.basepage.locators.newButton));
var locationLinkBtn = element(by.xpath(Objects.complaintPage.locators.locationsLinkBtn));
var addLocationBtn = element(by.css(Objects.complaintPage.locators.addLocationBtn));
var locationTypeDropDown = new SelectWrapper(by.model(Objects.complaintPage.locators.locationTypeDropDown));
var locationStreetInput = element(by.xpath(Objects.complaintPage.locators.locationStreetInput));
var locationCityInput = element(by.xpath(Objects.complaintPage.locators.locationCityInput));
var locationStateInput = element(by.xpath(Objects.complaintPage.locators.locationStateInput));
var locationZipInput = element(by.xpath(Objects.complaintPage.locators.locationZipInput));
var saveLocationBtn = element(by.css(Objects.complaintPage.locators.saveLocationBtn));
var address = element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(0);
var locationType = element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(1);
var locationCity = element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(2);
var locationState = element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(3);
var locationZip = element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(4);
var deleteLocationBtn = element(by.css(Objects.complaintPage.locators.deleteLocationBtn));
var editLocationBtn = element(by.css(Objects.complaintPage.locators.editLocationBtn));
var newComplaintBtn = element(by.css(Objects.complaintPage.locators.newComplaintBtn));
var complaintsTitle = element(by.xpath(Objects.complaintPage.locators.complaintsTitle));
var caseNumber = element(by.name(Objects.complaintPage.locators.caseNumber));
var searchButton = element(by.xpath(Objects.complaintPage.locators.searchButton));
var caseTitle = element(by.name(Objects.complaintPage.locators.caseTitle));
var caseCreatedDate = element(by.name(Objects.complaintPage.locators.caseCreatedDate));
var casePriority = element(by.name(Objects.complaintPage.locators.casePriority));
var selectParticipantType=element(by.xpath(Objects.complaintPage.locators.selectParticipantType));
var selectparticipant = element(by.name(Objects.casepage.locators.selectParticipant));
var searchForUserInput = element(by.xpath(Objects.casepage.locators.searchForUserInput));
var searchForUserBtn = element(by.buttonText(Objects.casepage.locators.searchUserBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUserName));
var okBtn = element(by.buttonText(Objects.casepage.locators.OkBtn));

var ComplaintPage = function() {

    browser.ignoreSynchronization = true;
    this.clickComplaintButton = function() {
        browser.ignoreSynchronization = false;
        browser.wait(EC.presenceOf(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000, "New Complaint button is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000, "New complaint button is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000, "New complaint button is not clickable").then(function() {
                    complaintButton.click();
                });
            });
        });


        return this;
    };

    this.submitInitiatorInformation = function(name, surname) {
        this.clickRadioBtnNewInitiator();
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "First name field is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "First name field is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "First name field is not clickable").then(function() {
                    firstName.click().then(function() {
                        firstName.clear();
                        firstName.sendKeys(name).then(function() {
                            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.lastName))), 30000, "Last name field is not visible").then(function() {
                                lastName.click().then(function() {
                                    lastName.sendKeys(surname);
                                });
                            });
                        });
                    });
                });
            });
        });
        return this;
    };

    this.clickNextButton = function() {
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "First name field is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "Frist name field is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000, "First name field is not clickable").then(function() {
                    nextButton.click();
                });
            });
        });
    };

    this.selectIncidentCategory = function(category) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000, "Incident category drop down list is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000, "Incident category drop down list is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000, "Incident cateogyr drop donw list is not clickable").then(function() {
                    incidentCategoryDDListBox.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.linkText(category))), 30000, category + " link is not visible in incident category drop down list").then(function() {
                            var incidentCategory = element(by.linkText(category));
                            incidentCategory.click();
                        });
                    });

                });
            });

        });
        return this;
    };

    this.insertTitle = function(title) {
        browser.wait(EC.visibilityOf(element(By.name(Objects.complaintPage.locators.complaintTitle))), 10000, "Complaint title filed is not displayed").then(function() {
            complaintTitle.click().then(function() {
                complaintTitle.sendKeys(title);
            });
        });
        return this;
    };
    this.insertIncidentInformation = function(category, title) {
        this.selectIncidentCategory(category);
        this.insertTitle(title);
        return this;
    };
    this.clickTab = function(tabname) {
        switch (tabname) {
            case "Initiator":
                initiatorTab.click();
                break;
            case "Incident":
                incidentTab.click();
                break;
            case "People":
                peopleTab.click();
                break;
            case "Attachments":
                attachmentsTab.click();
                break;
            case "Participants":
                participantsTab.click();
                break;
            default:
                break;
        }
        return this;
    };
    this.clickSubmitButton = function() {
        browser.wait(EC.invisibilityOf(element(by.xpath(Objects.basepage.locators.fadeElementDeleteNote))), 30000, "Animation element is visible").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.submitButton))), 30000, "Submit button is not visible ").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.submitButton))), 30000, "Submit button is not clickable").then(function() {
                    submitButton.click();
                });
            });
        });
        return this;
    };
    this.clickRadioBtnNewInitiator = function() {
        browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.radioButtonNewInitiator))), 30000, "Radio button for new initiator is not clickable").then(function() {
            radioButtonNewInitiator.click();
        });
        return this;
    };
    this.returnFirstNameValue = function() {
        return firstName.getAttribute("value");
    };
    this.returnLastNameValue = function() {
        return lastName.getAttribute("value");
    };
    this.reenterFirstName = function(name) {
        if (this.returnFirstNameValue() == "") {
            firstName.sendKeys(name);
        }
        return this;
    };
    this.clickCloseComplaint = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000, "Close complaint button is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000, "Close complaint button is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000, "Close complaint button is not clickable").then(function() {
                    closeComplaintButton.click();
                });
            });
        });
        return this;
    };
    this.selectComplaintDisposition = function(disposition) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000, "Complaint disposition drop down list is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000, "Complaint disposition drop down list is not visible").then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000, "Complaint disposition drop down list is not clickable").then(function() {
                    complaintDispositionDDListBox.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.linkText(disposition))), 30000, disposition + " is not visible in complaint disposition drop down list").then(function() {
                            browser.wait(EC.elementToBeClickable(element(by.linkText(disposition))), 30000, disposition + " is not clickable is complaint disposition drop down list").then(function() {
                                var complaintDisposition = element(by.linkText(disposition));
                                complaintDisposition.click();
                            });
                        });
                    });
                });
            });
        });
        return this;
    };
    this.insertCloseComplaintDescription = function(description) {
        browser.wait(EC.presenceOf(element(by.css(Objects.complaintPage.locators.closeComplaintDescription))), 30000, "Close complaint description field is not present in DOM").then(function() {
            closeComplaintDescription.click().then(function() {
                closeComplaintDescription.sendKeys(description);
            });
        });
        return this;
    };
    this.closeComplaint = function(disposition, description, approver) {
        this.selectComplaintDisposition(disposition);
        this.selectApprover(approver);
        //this.insertCloseComplaintDescription(description);
        this.clickSubmitButton();
        return this;
    };

    this.waitForComplaintTitle = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000, "Complaint title is not present in DOM").then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000, "Complaint title is not visible");
        })
        return this;
    };

    this.addLocation = function(type, street, city, state, zip) {

        browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.locationsLinkBtn))), 30000, "Locations link button is not visible").then(function() {
            locationLinkBtn.click().then(function() {
                browser.sleep(5000);
                browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.addLocationBtn))), 3000, "Add location button is not visible").then(function() {
                    addLocationBtn.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.model(Objects.complaintPage.locators.locationTypeDropDown))), 10000, "Location type drop down is not displayed").then(function() {
                            locationTypeDropDown.selectByText(type).then(function() {
                                locationStreetInput.sendKeys(street).then(function() {
                                    locationCityInput.sendKeys(city).then(function() {
                                        locationStateInput.sendKeys(state).then(function() {
                                            locationZipInput.sendKeys(zip).then(function() {
                                                saveLocationBtn.click().then(function() {
                                                    browser.wait(EC.visibilityOf(element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).get(0)), 30000, "Location is not added");
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
    };
    this.deleteLocation = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.deleteLocationBtn))), 30000, "Delete location button is not visible").then(function() {
            deleteLocationBtn.click().then(function() {
                browser.sleep(5000);
                element.all(by.repeater(Objects.complaintPage.locators.addedLocations)).then(function(items) {
                    expect(items.length).toBe(0, "The organization is not deleted");
                });
            });
        });
        return this;
    }


    this.editLocation = function(type, street, city, state, zip) {

        editLocationBtn.click().then(function() {
            browser.wait(EC.visibilityOf(element(by.model(Objects.complaintPage.locators.locationTypeDropDown))), 10000, "Location type drop down is not displayed").then(function() {
                locationTypeDropDown.selectByText(type).then(function() {
                    locationStreetInput.clear().then(function() {
                        locationStreetInput.sendKeys(street).then(function() {
                            locationCityInput.clear().then(function() {
                                locationCityInput.sendKeys(city).then(function() {
                                    locationStateInput.clear().then(function() {
                                        locationStateInput.sendKeys(state).then(function() {
                                            locationZipInput.clear().then(function() {
                                                locationZipInput.sendKeys(zip).then(function() {
                                                    saveLocationBtn.click().then(function() {
                                                        browser.wait(EC.textToBePresentInElement((locationType), type), 10000, "Location is not updated");
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
        return this;
    }

    this.waitForComplaintsPage = function() {
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintsTitle))), 40000, "Complaint title is not displayed");
    }


    this.returnLocationAddress = function() {
        return address.getText();
    }

    this.returnLocationType = function() {
        return locationType.getText();
    }
    this.returnLocationCity = function() {
        return locationCity.getText();
    }
    this.returnLocationState = function() {
        return locationState.getText();
    }
    this.returnLocationZip = function() {
        return locationZip.getText();
    }

    this.verifyIfAddLocationsBtnIsDisplayed = function() {
        expect(addLocationBtn.isDisplayed()).toBe(false, "Add lcaotion button it should not be displayed");
    }

    this.participantsTab = function() {
        participantsTab.click();
    }

    this.clickNewComplaintBtn = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.newComplaintBtn))), 30000, "New complaint button is not visible").then(function() {
            newComplaintBtn.click().then(function() {});
        });
    }


    this.getComplaintId = function() {
        return complaintID.getText();
    };

    this.returnComplaintType = function() {
        return complaintType.getText();
    };
    this.returnComplaintPriority = function() {
        return complaintPriority.getText();
    };
    this.returnCreatedDate = function() {
        return complaintCreateDate.getText();
    };
    this.returnComplaintTitle = function() {
        return complaintTitleSaved.getText();
    };
    this.returnComplaintsTitle = function() {
        return complaintsTitle.getText();
    };
    this.insertCaseNumber = function(caseid) {
        caseNumber.click();
        caseNumber.sendKeys(caseid);
        return this;
    };
    this.clickSearchButton = function() {
        searchButton.click();
        return this;
    };
    this.returnCaseTitle = function() {
        return caseTitle.getAttribute("value");
    };
    this.returnCaseCreatedDate = function() {
        return caseCreatedDate.getAttribute("value");
    };
    this.returnCasePriority = function() {
        return casePriority.getAttribute("value");
    }

    this.selectParticipant = function(type, participant) {

        var participantType = element(by.linkText(type));
        browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.selectParticipantType))), 10000).then(function() {
            selectParticipantType.click().then(function() {
                participantType.click().then(function() {
                    selectparticipant.click().then(function() {
                        browser.driver.switchTo().defaultContent();
                        browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchForUserInput))), 10000, "Search for user input is not displayed").then(function() {
                            searchForUserInput.sendKeys(participant).then(function() {
                                searchForUserBtn.click().then(function() {
                                    browser.wait(EC.visibilityOf(element(by.xpath(Objects.casepage.locators.searchedUserName))), 30000, "Searched user is not displayed").then(function() {
                                        searchedUser.click().then(function() {
                                            okBtn.click();
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
    };

};
ComplaintPage.prototype = basePage;
module.exports = new ComplaintPage();
