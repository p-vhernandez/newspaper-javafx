###### We will describe the functional requirements of the web interface of the newspaper desktop application.

This application allows manage and read articles.

**Project info**

The mandatory version will work with only one user. The user and password will be provided for each group. See the code documentation to know how to use it. When user selects ‘login’, this version will use the provided user and password. Maximum grade for mandatory version is eight points.

The extra exercise consists in define and use a login form. In this case, students will be provided with two or more users and passwords. In this scenario, when user selects ‘login’ a new form will be displayed. This form will provide all functionalities needed for login process. Maximum grade for mandatory version plus extra exercise eight points.

[News manager demo](https://web.microsoftstream.com/video/41cf1813-3f3a-47a1-9f56-b63c0696a6c7)

[Provided code](https://web.microsoftstream.com/video/7b42b2a5-fbcd-41b5-b9b0-1f61b9db73ef)

[JavaFX project](https://web.microsoftstream.com/video/bcb95dca-56d9-4ee4-8165-8a3288d89087)

**Type of users**
- Public users (no login needed)
    - Access to all published articles
    - See articles details
    - Create a new article, only saving it to a file
- Registered users (login required)
    - Access to all published articles
    - Edit articles that belong to the user
    - Create a new article
    - Delete articles that belong to the user
  
  
**Main window**
- [x] List of published articles (login is not required)
- [ ] Login
- [ ] New:
    - Login is not required
    - Allows to create a new article
    - Only logged users can send articles to server
    - Users can save article to a file (login is not required)
- [x] Load an article from a file
- [x] Edit:
    - Login is required
    - Only articles belonging to the user can be edited
- [x] Delete:
    - Login is required
    - Only articles belonging to the user can be deleted
- [x] Exit
- [x] Headlines list:
    - Shows a list of headlines
    - When user selects a headline:
        - [x] Command "read more" will be enabled
        - [x] Article image will be shown
        - [x] Abstract will be shown
        - [x] If the article belongs to the user, "edit" will be enabled
        - [x] If the article belongs to the user, "delete" will be enabled
- [x] Category filter:
    - Filter headlines that will be listed 
    - Only headlines with the selected category will be listed
    - If category is equal to "ALL", all headlines will be listed
- [x] Read more:
    - Allows user to acces the selected article details
    
[Main window structure](resources/images/git/main-window.PNG)

**Form for article details**
- [x] Always display article details
- [x] At the beginning, this form shows the article body
- [x] Switch between body and abstract
- [x] Back to the previous form

[Article details window structure](resources/images/git/article-details.PNG)

**Form for editing and creating articles**

This screen provides services for:
- [x] Add an image:
    - The provided form "ImagePicker" can be used to implement this functionality
- [x] Add title
- [x] Add subtitle
- [x] Set article's category
- [x] Add abstract:
    - Can be in plain text or HTML
- [x] Add body:
    - Can be in plain text or HTML
- [ ] Send the new or modify the existing to the server and go back to the main window. (Title and category must have been defined)
- [x] Save a draft to a file:
    - This command saves the article to a file in the local machine
    - This draft could be loaded again for editing or send it to a server
    - To save an article, title must have been defined
- [x] Back:
    - This command discards all changes made since last "save to file" command
    
[Edit & create window structure](resources/images/git/edit-and-create-news.PNG)

**Other**
- [ ] In the main page, if an article does not have an image, show the default one. 
