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
var selectApprover = element(by.xpath(Objects.casepage.locators.selectApprover));
var searchForUser = element(by.xpath(Objects.casepage.locators.searchForUser));
var goBtn = element(by.xpath(Objects.casepage.locators.goBtn));
var addBtn = element(by.xpath(Objects.casepage.locators.addBtn));
var searchedUser = element(by.xpath(Objects.casepage.locators.searchedUser));
var newBtn = element(by.xpath(Objects.basepage.locators.newButton));
var locationLinkBtn = element(by.xpath(Objects.complaintPage.locators.locationsLinkBtn));
var addLocationBtn = element(by.css(Objects.complaintPage.locators.addLocationBtn));
var locationTypeDropDown = element(by.model(Objects.complaintPage.locators.locationTypeDropDown));
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



var ComplaintPage = function() {

    browser.ignoreSynchronization = true;
    this.clickComplaintButton = function() {
        browser.ignoreSynchronization = false;
        browser.wait(EC.presenceOf(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.linkText(Objects.complaintPage.locators.complaintButton))), 30000).then(function() {
                    complaintButton.click();
                });
            });
        });


        return this;
    };

    this.submitInitiatorInformation = function(name, surname) {
        this.clickRadioBtnNewInitiator();
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
                    firstName.click().then(function() {
                        firstName.clear();
                        firstName.sendKeys(name);
                    })
                });
            });
        });
        browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.lastName))), 30000).then(function() {
            lastName.click().then(function() {
                lastName.sendKeys(surname);
            })
        });
        return this;
    };

    this.clickNextButton = function() {
        browser.wait(EC.presenceOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.name(Objects.complaintPage.locators.firstName))), 30000).then(function() {
                    nextButton.click();
                });
            });
        });
    };

    this.selectIncidentCategory = function(category) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.incidentCategoryDDListBox))), 30000).then(function() {
                    incidentCategoryDDListBox.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.linkText(category))), 30000).then(function() {
                            var incidentCategory = element(by.linkText(category));
                            incidentCategory.click();
                        })


                    });

                })
            })

        })
        return this;
    };

    this.insertTitle = function(title) {
        complaintTitle.click();
        complaintTitle.clear();
        complaintTitle.sendKeys(title);
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
        submitButton.click();
        return this;
    };
    this.clickRadioBtnNewInitiator = function() {
        browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.radioButtonNewInitiator))), 30000).then(function() {
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
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.closeComplaintButton))), 30000).then(function() {
                    closeComplaintButton.click();
                });
            });
        });
        return this;
    };
    this.selectComplaintDisposition = function(disposition) {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000).then(function() {
                browser.wait(EC.elementToBeClickable(element(by.xpath(Objects.complaintPage.locators.complaintDispositionDDListBox))), 30000).then(function() {
                    complaintDispositionDDListBox.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.linkText(disposition))), 30000).then(function() {
                            var complaintDisposition = element(by.linkText(disposition));
                            complaintDisposition.click();
                        });
                    });
                });
            });
        });
        return this;
    };
    this.insertCloseComplaintDescription = function(description) {
        closeComplaintDescription.sendKeys(description);
        return this;
    };
    this.closeComplaint = function(disposition, description, approver) {
        this.selectComplaintDisposition(disposition);
        this.selectApprover(approver);
        this.insertCloseComplaintDescription(description);
        this.clickSubmitButton();
        return this;
    };

    this.addLocation = function(type, street, city, state, zip) {


        browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.locationsLinkBtn))), 30000).then(function() {
            locationLinkBtn.click().then(function() {
                browser.sleep(5000);
                browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.addLocationBtn))), 3000).then(function() {
                    addLocationBtn.click().then(function() {
                        browser.wait(EC.visibilityOf(element(by.model(Objects.complaintPage.locators.locationTypeDropDown))), 10000, "Location type drop down is not displayed").then(function() {
                            locationTypeDropDown.$('[value="string:' + type + '"]').click().then(function() {
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
    this.waitForComplaintTitle = function() {
        browser.wait(EC.presenceOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000).then(function() {
            browser.wait(EC.visibilityOf(element(by.xpath(Objects.complaintPage.locators.complaintTitleLink))), 30000);
        })
    };
    this.editAssignee = function(assignee) {

        assigneeLink.click().then(function() {
            browser.wait(EC.presenceOf(element(by.xpath("//*[@class='clearfix']/div[3]/div[1]/div/form/div/select/option[8]"))), 5000).then(function() {
                assigneeDropDown.$('[value="string:' + assignee + '"]').click().then(function() {
                    assigneeBtn.click();
                });
            });
        });
    }


    this.deleteLocation = function() {

        browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.deleteLocationBtn))), 30000).then(function() {
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
                locationTypeDropDown.$('[value="string:' + type + '"]').click().then(function() {
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

        browser.wait(EC.visibilityOf(element(by.css(Objects.complaintPage.locators.newComplaintBtn))), 30000).then(function() {
            newComplaintBtn.click().then(function() {});
        });
    }

    this.returnComplaintsTitle = function() {
        return complaintsTitle.getText();
    }

};
ComplaintPage.prototype = basePage;
module.exports = new ComplaintPage();
