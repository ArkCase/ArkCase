package com.armedia.acm.plugins.onlyoffice.model.config;

public class EditorCustomization
{
    /**
     * Defines if the Autosave menu option is enabled or disabled. If set to false, only Strict co-editing mode can be
     * selected, as Fast does not work without autosave. Please note that in case you change this option in menu it will
     * be saved to your browser localStorage. The default value is true.
     */
    private boolean autosave;
    /**
     * Defines if the Chat menu button is displayed or hidden; please note that in case you hide the Chat button, the
     * corresponding chat functionality will also be disabled. The default value is true.
     */
    private boolean chat;
    /**
     * Defines if the user can edit only his comments. The default value is false.
     */
    private boolean commentAuthorOnly;
    /**
     * Defines if the Comments menu button is displayed or hidden; please note that in case you hide the Comments
     * button, the corresponding commenting functionality will be available for viewing only, the adding and editing of
     * comments will be unavailable. Deprecated since version 4.4, please use document.permissions.comment instead.
     */
    private boolean comments;
    /**
     * Defines if the top toolbar type displayed is full (false) or compact true. The default value is false.
     */
    private boolean compactToolbar;
    /**
     * Contains the information for the editor About section.
     */
    private Customer customer;
    /**
     * Defines settings for the Feedback & Support menu button. Can be either boolean (simply displays or hides the
     * Feedback & Support menu button) or object.
     */
    private Feedback feedback;
    /**
     * Adds the request for the forced file saving to the callback handler when saving the document within the document
     * editing service (e.g. clicking the Save button, etc.). The default value is false.
     */
    private boolean forcesave;
    /**
     * Defines settings for the Go to Documents menu button and upper right corner button.
     */
    private GoBack goback;
    /**
     * Changes the image file at the top left corner of the EditorConfig header. The recommended image height is 20
     * pixels.
     */
    private EditorLogo logo;
    /**
     * Defines if the review changes panel is automatically displayed or hidden when the editor is loaded. The default
     * value is false.
     */
    private boolean showReviewChanges;

    /**
     * Defines the document display zoom value measured in percent. Can take values larger than 0. For text documents
     * and presentations it is possible to set this parameter to -1 (fitting the document to page option) or to -2
     * (fitting the document page width to the editor page). The default value is 100.
     */
    private int zoom;

    public boolean isAutosave()
    {
        return autosave;
    }

    public void setAutosave(boolean autosave)
    {
        this.autosave = autosave;
    }

    public boolean isChat()
    {
        return chat;
    }

    public void setChat(boolean chat)
    {
        this.chat = chat;
    }

    public boolean isCommentAuthorOnly()
    {
        return commentAuthorOnly;
    }

    public void setCommentAuthorOnly(boolean commentAuthorOnly)
    {
        this.commentAuthorOnly = commentAuthorOnly;
    }

    public boolean isComments()
    {
        return comments;
    }

    public void setComments(boolean comments)
    {
        this.comments = comments;
    }

    public boolean isCompactToolbar()
    {
        return compactToolbar;
    }

    public void setCompactToolbar(boolean compactToolbar)
    {
        this.compactToolbar = compactToolbar;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public void setCustomer(Customer customer)
    {
        this.customer = customer;
    }

    public Feedback getFeedback()
    {
        return feedback;
    }

    public void setFeedback(Feedback feedback)
    {
        this.feedback = feedback;
    }

    public boolean isForcesave()
    {
        return forcesave;
    }

    public void setForcesave(boolean forcesave)
    {
        this.forcesave = forcesave;
    }

    public GoBack getGoback()
    {
        return goback;
    }

    public void setGoback(GoBack goback)
    {
        this.goback = goback;
    }

    public EditorLogo getLogo()
    {
        return logo;
    }

    public void setLogo(EditorLogo logo)
    {
        this.logo = logo;
    }

    public boolean isShowReviewChanges()
    {
        return showReviewChanges;
    }

    public void setShowReviewChanges(boolean showReviewChanges)
    {
        this.showReviewChanges = showReviewChanges;
    }

    public int getZoom()
    {
        return zoom;
    }

    public void setZoom(int zoom)
    {
        this.zoom = zoom;
    }
}
