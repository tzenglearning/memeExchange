(function() {

  /**
   * Variables
   */
  var user_id = '1111';
  var user_fullname = 'John';
  var lng = -122.08;
  var lat = 37.38;
  
  var mock_recommend_data = [{"id": "1", "image_url": "//storage.googleapis.com/meme_generator/mememasterCS701.png", 
                "caption": "Hello EveryOne","userId":"mememaster", "favorite": true, "numberOfLikes":"5", "follow":false},
                {"id": "2", "image_url": "//storage.googleapis.com/meme_generator/blb.png", "caption": "Hello EveryOne","userId":"lol","favorite": false,"numberOfLikes":"5","follow":false}];

  /**
   * Initialize major event handlers
   */
  function init() {
    // register event listeners
    
    document.querySelector('#create-btn').addEventListener('click', loadTemplates);
    document.querySelector('#login-form-btn').addEventListener('click', onSessionInvalid);
    document.querySelector('#login-btn').addEventListener('click', login);
    document.querySelector('#register-form-btn').addEventListener('click', showRegisterForm);
    document.querySelector('#register-btn').addEventListener('click', register);
    document.querySelector('#following-btn').addEventListener('click', loadFollowingItems);
    document.querySelector('#recommend-btn').addEventListener('click', loadRecommendedItems);
    document.querySelector('#avatar').addEventListener('click', function(){loadProfile("")});
    document.querySelector('#explore-btn').addEventListener('click', showExplorePage);
    document.querySelector("#search-btn").addEventListenr('click', loadUsers);
    validateSession();
    //SessionValid({"user_id":"1111","name":"John Smith","status":"OK"});
    //onSessionInvalid();
  }

  /**
   * Session
   */
  function validateSession() {
    onSessionInvalid();
    // The request parameters
    var url = './login';
    var req = JSON.stringify({});

    // display loading message
    //showLoadingMessage('Validating session...');

    // make AJAX call
    ajax('GET', url, req,
      // session is still valid
      function(res) {
        var result = JSON.parse(res);

        if (result.status === 'OK') {
          onSessionValid(result);
        }
      });
  }

  function onSessionValid(result) {
    user_id = result.user_id;
    user_fullname = result.name;

    var loginForm = document.querySelector('#login-form');
    var registerForm = document.querySelector('#register-form');
    var memes = document.querySelector('#memes');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    var avatar = document.querySelector('#avatar');
    var welcomeMsg = document.querySelector('#welcome-msg');
    var logoutBtn = document.querySelector('#logout-link');
    var templates = document.querySelector('#templates');
    var explorePage = document.querySelector('#explore-page');

    welcomeMsg.innerHTML = 'Welcome, ' + user_id;
    loadRecommendedItems();
    

    showElement(memes);
    showElement(avatar);
    showElement(welcomeMsg);
    showElement(logoutBtn, 'inline-block');
    hideElement(loginForm);
    hideElement(registerForm);
    hideElement(templates);
    hideElements(explorePage);
  }

  function onSessionInvalid() {
    var loginForm = document.querySelector('#login-form');
    var registerForm = document.querySelector('#register-form');
    var memes = document.querySelector('#memes');
    var avatar = document.querySelector('#avatar');
    var welcomeMsg = document.querySelector('#welcome-msg');
    var logoutBtn = document.querySelector('#logout-link');
    var templates = document.querySelector('#templates');
    var explorePage = document.querySelector('#explore-page');

    hideElement(memes);
    hideElement(avatar);
    hideElement(logoutBtn);
    hideElement(welcomeMsg);
    hideElement(registerForm);
    hideElement(templates);
    hideElement(explorePage);

    clearLoginError();
    showElement(loginForm);
  }

  function hideElement(element) {
    if(element != null){
      element.style.display = 'none';
    } else {
      console.log("element is null");
    }
  }

  function showElement(element, style) {
    var displayStyle = style ? style : 'block';
    element.style.display = displayStyle;
  }
  
  function showRegisterForm() {
    var loginForm = document.querySelector('#login-form');
    var registerForm = document.querySelector('#register-form');
    var memes = document.querySelector('#memes');
    var avatar = document.querySelector('#avatar');
    var welcomeMsg = document.querySelector('#welcome-msg');
    var logoutBtn = document.querySelector('#logout-link');
    var templates = document.querySelector('#templates');
    var explorePage = document.querySelector('#explore-page');

    hideElement(memes);
    hideElement(avatar);
    hideElement(logoutBtn);
    hideElement(welcomeMsg);
    hideElement(loginForm);
    hideElement(templates);
    hideElement(explorePage);
    
    clearRegisterResult();
    showElement(registerForm);
  }  
  
  // -----------------------------------
  // Login
  // -----------------------------------

  function login() {
    var username = document.querySelector('#username').value;
    var password = document.querySelector('#password').value;
    password = md5(username + md5(password));

    // The request parameters
    var url = './login';
    var req = JSON.stringify({
      user_id : username,
      password : password,
    });

    ajax('POST', url, req,
      // successful callback
      function(res) {
        var result = JSON.parse(res);

        // successfully logged in
        if (result.status === 'OK') {
          onSessionValid(result);
        }
      },

      // error
      function() {
        showLoginError();
      },
      true);
  }

  function showLoginError() {
    document.querySelector('#login-error').innerHTML = 'Invalid username or password';
  }
  
  function clearLoginError() {
    document.querySelector('#login-error').innerHTML = '';
  }

  // -----------------------------------
  // Register
  // -----------------------------------

  function register() {
    var username = document.querySelector('#register-username').value;
    var password = document.querySelector('#register-password').value;
    var firstName = document.querySelector('#register-first-name').value;
    var lastName = document.querySelector('#register-last-name').value;
    
    if (username === "" || password == "" || firstName === "" || lastName === "") {
    	showRegisterResult('Please fill in all fields');
    	return;
    }
    
    if (username.match(/^[a-z0-9_]+$/) === null) {
    	showRegisterResult('Invalid username');
    	return;
    }
    
    password = md5(username + md5(password));

    // The request parameters
    var url = './register';
    var req = JSON.stringify({
      user_id : username,
      password : password,
      first_name: firstName,
      last_name: lastName,
    });

    ajax('POST', url, req,
      // successful callback
      function(res) {
        var result = JSON.parse(res);

        // successfully logged in
        if (result.status === 'OK') {
        	showRegisterResult('Succesfully registered');
        } else {
        	showRegisterResult('User already existed');
        }
      },

      // error
      function() {
    	  showRegisterResult('Failed to register');
      },
      true);
  }

  function showRegisterResult(registerMessage) {
    document.querySelector('#register-result').innerHTML = registerMessage;
  }

  function clearRegisterResult() {
    document.querySelector('#register-result').innerHTML = '';
  }

  // -----------------------------------
  // Helper Functions
  // -----------------------------------

  /**
   * A helper function that makes a navigation button active
   *
   * @param btnId - The id of the navigation button
   */
  function activeBtn(btnId) {
    var btns = document.querySelectorAll('.main-nav-btn');

    // deactivate all navigation buttons
    for (var i = 0; i < btns.length; i++) {
      btns[i].className = btns[i].className.replace(/\bactive\b/, '');
    }

    // active the one that has id = btnId
    var btn = document.querySelector('#' + btnId);
    btn.className += ' active';
  }

  function showLoadingMessage(msg) {
    var memes = document.querySelector('#memes');
    memes.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i> ' +
      msg + '</p>';
  }

  function showWarningMessage(msg) {
    var itemList = document.querySelector('#item-list');
    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i> ' +
      msg + '</p>';
  }

  function showErrorMessage(msg) {
    var itemList = document.querySelector('#memes');
    itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' +
      msg + '</p>';
  }

  /**
   * A helper function that creates a DOM element <tag options...>
   * @param tag
   * @param options
   * @returns {Element}
   */
  function $create(tag, options) {
    var element = document.createElement(tag);
    for (var key in options) {
      if (options.hasOwnProperty(key)) {
        element[key] = options[key];
      }
    }
    return element;
  }

  /**
   * AJAX helper
   *
   * @param method - GET|POST|PUT|DELETE
   * @param url - API end point
   * @param data - request payload data
   * @param successCallback - Successful callback function
   * @param errorCallback - Error callback function
   */
  function ajax(method, url, data, successCallback, errorCallback) {
    var xhr = new XMLHttpRequest();
    console.log(data);
    xhr.open(method, url, true);
    xhr.onload = function() {
      console.log(xhr.status);
      if (xhr.status === 200) {
        console.log("hi");
        successCallback(xhr.responseText);
      } else {
        errorCallback();
      }
    };

    xhr.onerror = function() {
      console.error("The request couldn't be completed.");
      errorCallback();
    };

    if (data === null) {
      xhr.send();
    } else {
      xhr.setRequestHeader("Content-Type",
        "application/json;charset=utf-8");
      xhr.send(data);
    }
  }

  // -------------------------------------
  // AJAX call server-side APIs
  // ------------------------------------- 

  /**
   * API #1 Load the templates API end point: [GET]
   * /templates
   */
  function loadTemplates() {
    var memes = document.querySelector('#memes');
    var explorePage = document.querySelector('#explore-page');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    
    
    hideElement(memes);
    hideElement(explorePage);
    hideElement(profileContainer);
    showElement(templates);
    
    
    
    // The request parameters
    var url = './templates';
    var params = '';
    var req = JSON.stringify({});
    
    // display loading message
   // showLoadingMessage('Loading ...');

    // make AJAX call
    ajax('GET', url, req,
       // successful callback
       function(res) {
         var items = JSON.parse(res);
         console.log(items);
         if (!items || items.length === 0) {
           console.log("No templates.");
           showWarningMessage('No templates.');
         } else {
           listTemplates(items);
         }
       },
       // failed callback
       function() {
         console.log('Cannot load templates.');
       }
     );
  }

  /**
   * API #2 Load favorite (or visited) items API end point: [GET]
   * /history?user_id=1111
   */
  function loadFollowingItems() {
    var memes = document.querySelector('#memes');
    var explorePage = document.querySelector('#explore-page');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    
    
    showElement(memes);
    hideElement(explorePage);
    hideElement(profileContainer);
    hideElement(templates);
    
    // request parameters
    var url = './feed';
    var params = '';
    var req = JSON.stringify({});

    // display loading message
    //showLoadingMessage('Loading Following items...');

    // make AJAX call
     ajax('GET', url + '?' + params, req, function(res) {
       var items = JSON.parse(res);
       if (!items) {
         showWarningMessage('No favorite item.');
       } else {
         listMemes(items, "recommend");
       }
     }, function() {
       showErrorMessage('Cannot load Following items.');
     });
 }

  // -------------------------------------
  // Profile page frontend
  // ------------------------------------- 
  /*
  Calls the relevant functions to display the profile page:
    populateProfileHeader
    listProfileMems
  */ 
  function loadProfile(name) {
  
    var memes = document.querySelector('#memes');
    var explorePage = document.querySelector('#explore-page');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    
    
    hideElement(memes);
    hideElement(explorePage);
    hideElement(templates);
    showElement(profileContainer);

    
    var userId = name;
    if(name === ""){
      userId = document.querySelector("#welcome-msg").innerHTML.substring(9);
    }
    
    // request parameters
    var url = './create';
    var params = 'userId=' + userId;
    var req = "";

    // make AJAX call
     ajax('GET', url + '?' + params, req, function(res) {
       var items = JSON.parse(res);
       if (!items || items.length == 0) {
          showWarningMessage('No items.');
       } else {
    		populateProfileHeader(items);
    		listProfileMemes(items); 
       }
     }, function() {
       showErrorMessage('Cannot load profile.');
     });
     

  }
  
  //load explore page
  function showExplorePage(){
    var memes = document.querySelector('#memes');
    var explorePage = document.querySelector('#explore-page');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    
    
    hideElement(memes);
    showElement(explorePage);
    hideElement(profileContainer);
    hideElement(templates);
    
    explorePage.innerHTML = "";
    explorePage.style.display = "flex";
 }
 
 function loadUsers(userId){   
    // request parameters
    var url = './user';
    var params = 'userId= ' + userId;
    var req = JSON.stringify({});

    // display loading message
    //showLoadingMessage('Loading users...');

    // make AJAX call
     ajax('GET', url + '?' + params, req, function(res) {
       var items = JSON.parse(res);
       if (!items) {
         showWarningMessage('No such user.');
       } else {
         var list = document.querySelector("#userResult");
         listUsers(list, items);
       }
     }, function() {
       showErrorMessage('Cannot Users.');
     });
 }
 
 function listUsers(list, items){
    var list = $create("ul", {id: "item-list"});
    
    for(let i = 0; i < list.length; i++){
        let item  = list[i];
    	let listItem = $create("li", {className: "item"});
    	var user = $create("img", {src: item.image_url});
    	var userId = $create("h2");
        userId.innerHTML = item.userId;   
        listItem.appendChild(user);
        listItem.appendChild(userId);
        list.appendChild(item1);
    }
    
    var userResult = document.querySelector("#userResult");
    userResult.appendChild(list);    
 }
  /*
 Profile: {“author_id”: … , “numOfMemes” …, “numberOfFollwers”:… , “numberOfFollowing”: …, ”memes”:jsonarray}
  */
  function populateProfileHeader(data) { 
    var numFollowers = data.numOfFollowers;
    var numMemes = data.numOfMemes;
    var numFollowing = data.numOfFollowing;
    
    profileContainer.innerHTML = '';
    showElement(profileContainer);
    var profile = $create('div', {});
    profile.setAttribute("class", "profile");
    
    populateProfileImage(profile, data.profilePicture);
    populateProfileUserSettings(profile, data.author_id);
    populateProfileStats(profile, numMemes, numFollowing, numFollowers);
    profileContainer.appendChild(profile);
  }
  
  function populateProfileImage(profile, profilePicture) {
    var profileImage = $create('div', {});
    profileImage.setAttribute("class", "profile-image");
    var image = $create('img', {src: profilePicture}); //hardcode
    profileImage.appendChild(image);
    profile.appendChild(profileImage);
  }
  
  function populateProfileUserSettings(profile, authorId) {
    var profileUserSettings = $create('div', {});
    profileUserSettings.setAttribute("class", "profile-user-settings");
    var profileUserName = $create('h1', {});
    profileUserName.setAttribute("class", "profile-user-name");
    
    let userName = authorId;
    profileUserName.textContent += '@' + userName;
    profileUserSettings.appendChild(profileUserName);
    profile.appendChild(profileUserSettings);
  }
  
  function populateProfileStats(profile, numPosts, numFollowing, numFollowers) {
    var profileStats = $create('div', {});
    profileStats.setAttribute("class", "profile-stats");
    var posts = $create('li', {});
    var followers = $create('li', {});
    var following = $create('li', {});
    posts.setAttribute("class", "profile-stat-count");
    followers.setAttribute("class", "profile-stat-count");
    following.setAttribute("class", "profile-stat-count");
    
    posts.textContent += "Posts " + numPosts;
    followers.textContent += "Followers " + numFollowers;
    following.textContent += "Following " + numFollowing;
    
    profileStats.appendChild(posts);
    profileStats.appendChild(followers);
    profileStats.appendChild(following);    
    profile.appendChild(profileStats);
  }
  
  function populateMemesOnProfile(data) {
    showElement(memes);
    
    var images = document.querySelector('#memes');
    images.innerHTML = '';
    listMemes(data.memes, "create");
    
  }
  
  function listProfileMemes(data) { 
    prepProfilePage();
    populateMemesOnProfile(data);
  }
  
  /* prepProfilePage():
   * clears all elements (except for the header) off of the page 
   * for a clean slate. 
   */
  function prepProfilePage() {
    var tmps = document.querySelectorAll('templates');
    for(var i = 0; i < tmps.length; i++) {
      hideElement(tmps[i]);
    }
}
  
  /**
   * API #3 Load recommended items API end point: [GET]
   * /recommendation?user_id=1111
   */
  function loadRecommendedItems() {
    var memes = document.querySelector('#memes');
    var explorePage = document.querySelector('#explore-page');
    var profileContainer = document.querySelector("#profileContainer");
    var templates = document.querySelector('#templates');
    
    
    showElement(memes);
    hideElement(explorePage);
    hideElement(templates);
    hideElement(profileContainer);
 
    // request parameters
    var url = './recommendation';
    var data = null;


    // make AJAX call
     ajax('GET', url, data,
       // successful callback
       function(res) {
         var items = JSON.parse(res);
         if (!items || items.length === 0) {
           showWarningMessage('No recommended item. Make sure you have favorites.');
         } else {
           listMemes(items, "recommend");
         }
       },
       // failed callback
       function() {
         console.log('Cannot load recommended items.');
       }
     );
    
    
  }

  /**
   * API #4 Toggle favorite (or visited) items
   *
   * @param item_id - The item business id
   *
   * API end point: [POST]/[DELETE] /history request json data: {
   * user_id: 1111, visited: [a_list_of_business_ids] }
   */
  function changeFavoriteItem(meme_id) {
    console.log("here");
    // check whether this item has been visited or not
    var figure = document.querySelector('#meme-' + meme_id);
    var favIcon = document.querySelector('#fav-icon-' + meme_id);
    var favorite = !(figure.dataset.favorite === 'true');
    
    // request parameters
    var url = './history';
    var req = JSON.stringify({
       meme_id: meme_id
    });
    var method = favorite ? 'POST' : 'DELETE';
    
    ajax(method, url, req,
        // successful callback
        function(res) {
         var result = JSON.parse(res);
         if (result.status === 'OK' || result.result === 'SUCCESS' || result.numberOFLikes != -1) {
              figure.dataset.favorite = favorite;
              favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
              favIcon.innerHTML = result.numberOfLikes;
         }
       });
    

  }
  
  function changeFriendship(author_id){
      
      var  author_list= document.querySelectorAll('.' + author_id);
      var  followed = author_list[0].dataset.followed == 'true';
    
        // request parameters
    var url = './friendship';
    var req = JSON.stringify({
       to_user_id: author_id
    });
    
    var method = (!followed) ? 'POST' : 'DELETE';

    ajax(method, url, req,
        // successful callback
        function(res) {
         var result = JSON.parse(res);
         if (result.status === 'OK' || result.result === 'SUCCESS') {
                 for (var i = 0; i < author_list.length; i++) {
                    var element = author_list[i];
                    element.dataset.followed = !followed;
                    element.className = (followed? 'fa fa-user-plus ' :'fas fa-user-friends ') + author_id;
        
                  }
         }
       });
     
  }

  // -------------------------------------
  // Create item list
  // -------------------------------------

  /*
  Adds the template images as figures to the document
  TODO: I need to refactor this for clarity...
  */
  function listTemplates(templates) { 
    var memes = document.querySelector('#memes');
    hideElement(memes);
    hideElement(profileContainer);
    
    var images = document.querySelector('#templates');
    images.innerHTML = '';
    images.className = 'templateDisplay';
    var row = $create('div', {});
    row.setAttribute("class", "row");  
    /*
    row
       column
           figure(id, className:)
    */
    console.log(templates);
    for (var i = 0; i < templates.length; i++) {
      let column = $create('div', {});
      column.setAttribute("class", "column");
      
      let image_url = '//'+templates[i].image_url;
      let name = image_url.substring(image_url.lastIndexOf('/')+1, image_url.lastIndexOf('.'));
      
      let template = $create('figure', {image_url: image_url});
      template.dataset.name = name;
      
      template.onclick = function(){displayTemplate2(template)};
     
      var img = $create('img', {src: image_url});
      template.appendChild(img);
      column.appendChild(template)
      row.appendChild(column);

      images.appendChild(row);
    } //end of for loop
       
  }
  
  /*
    a helper function that sends the image url and name to the displayCreatePage function 
  */
  function displayTemplate2(template) {
    var src = template.firstChild.src;
    let name = template.dataset.name;
    displayCreatePage(src, name);
  }
  
  /*
    this is unused - it is ray's code
  */
  function displayTemplate(template) { 
    console.log("displayTemplate");
    console.log("the template is:");
    console.log(template);
    var templates = document.querySelector('#templates');
    templates.innerHTML = ' ';
    templates.className = 'ui container';
    var rowsAndColumns = $create('div', {className: "ui grid stackable"});
    
    let row = $create('div', {className: "row"});

    let imageColumn = $create('div', {className: "eight wide column"});

    let card = $create('div', {className: "ui card"});
    
    let inputHeader= $create('div', {className: "content"});
    
    let header = $create('h2', {className: "header"});
    
    header.innerHTML = "Image";
    inputHeader.appendChild(header);
    
    let inputImage= $create('div', {className: "content"});
    let image =$create('img', {id: "iamge", className: "ui centered medium image" })
    
    inputImage.appendChild(image);
    
    card.appendChild(inputHeader);
    
    card.appendChild(inputImage);
    imageColumn.appendChild(card);
    
    let textColumn = $create('div', {className: "eight wide column"});
    let textCard = $create('div', {className: "ui card"});
    let textHeader= $create('div', {className: "content"});
    let editHeader = $create('h2', {className: "header"});
    editHeader.ineerHTML = "Edit";
    textHeader.appendChild(editHeader);
    let editInput = $create('div', {className: "content content-result"});
    textCard.appendChild(textHeader);
    textCard.appendChild(editInput);
    textColumn.appendChild(textCard);
    
    row.appendChild(imageColumn);
    row.appendChild(textColumn);
    
    rowsAndColumns.appendChild(row);
    templates.append(rowsAndColumns);
    
    
    

//     let id = template.id;
//     let image_url = template.image_url;
    
//     var figure= $create('figure', {id : id, image_url: image_url});
//     figure.dataset.id = id;
    
//     var img = $create('img', {src: image_url});
//     figure.appendChild(img);
//     column.appendChild(figure)
//     row.appendChild(column);

   
    

    
     
  }
  
  /*
    displayCreatePage(img, name)
    img: the image url that was chosen
    name: the image name that was chosen
    
    this function displays all of the elements of the create
    meme page - a header, the image, the text fields and a button
  */
  function displayCreatePage(img, name) {
    var templateContainer = document.querySelector('#templates');
    createHeader(templateContainer);
    var display = $create('div', {id: 'display'});
    display.setAttribute
    addFigure(img, display);
    
    var ids = ["upText", "downText", "category", "caption"];
    createTextFields(display, ids);
    templateContainer.appendChild(display);
    
    addCreateButton(templateContainer, ids, name);
  }
  
  /*
    addCreateButton:
    templateContainer: the container that the button will be added to 
    ids: the ids of the text fields
    name: the name of the template chosen
    
    this funciton adds a button for the user to click when the want to 
    "create" their meme. It compiles all of the relevant information and
    passes it to createMeme on click.
  */
  function addCreateButton(templateContainer, ids, name) {
    var footer = $create('div', {id: 'footer'});
    footer.setAttribute("class", "w3-container");
    var button = $create('button', {});
    button.setAttribute("class", "create-button")
    button.setAttribute("type", "button");
    button.textContent += "Create Meme";
    
    footer.appendChild(button);
    templateContainer.appendChild(footer);
    
    button.addEventListener('click',  () => { createMeme(ids, name); }, false);
  }
  

  /*
    createMemes: 
    ids: list of the ids of the text fiels elements
    name: name of the selected template
    
    this function gets the values of the text fields and the will pass
    this values along with the template name to create the meme
  */
  function createMeme(ids, name) {
    var container = display.lastChild;
    var values = ["", "", "", ""];
    for(var i = 0; i < ids.length; i++) {
      var temp = ids[i];
      var p = container.children[i];
      var input = p.children[1].value;
      values[i] = input;
    }

    var templateId = name;
    var upText = values[0];
    var downText = values[1];
    var category = values[2];
    var caption = values[3];

    var url = './create';
    var req = JSON.stringify({
        templateId : templateId, 
        category : category,
        caption: caption, 
        upText: upText, 
        downText: downText});
    console.log(req);
    ajax('POST', url, req,
      // successful callback
       function(res) {
         var result = JSON.parse(res);
         if (result && result.length > 0) {
           console.log("hii");
           hideElement(document.querySelector('#templates'));
           showElement(document.querySelector('#memes'));
           listMemes(result, "create")
         }
       });
  }
  
  /*
    addFigure(img, display)
    img: the image that was chosen by the user
    display: the sub view that the image will be added to
    
    this funcion adds the relevant image to the "create meme" view
  */
  function addFigure(img, display) {
    var figure = $create('img', {src: img});
    var template = $create('templates', {});
    template.setAttribute("float", "right");
    figure.setAttribute("class", "templates large");    
    template.appendChild(figure);
    
    display.appendChild(template);
  }
  
  /* 
    createTextFields(display)
    display: The subview that the TF will be added to
    
    This function adds the text fields to the subview display on the 
    create memes view
  */
  function createTextFields(display, ids) {
    var container = $create('form', {});
    container.setAttribute("class", "w3-container");
    var fields = ["Up Text", "Down Text", "Category", "Caption"];
    var textFields = $create('container', {});
    for(var i = 0; i <fields.length; i++) {
      var fieldP = $create('p', {id: ids[i]});
      var label = $create('label', {});
      label.textContent += fields[i];
      var input = $create('input', {});
      input.setAttribute("class", "w3-input");
      input.setAttribute("type", "text");
      fieldP.appendChild(label);
      fieldP.appendChild(input);
      textFields.appendChild(fieldP);
    }
    display.appendChild(textFields);
  }
  
  /*
    createHeader(container)
    container: the subview for the header to be added to
    
    this function adds a header to the create meme page on the 
    sub view specified by the container parameter.
  */
  function createHeader(container) {
    container.innerHTML = '';
    var header = $create('div', {id: 'Header'});
    header.setAttribute("class", "w3-container w3-blue");
    header.textContent += "Create Your Meme";
    container.appendChild(header);
  }
  
  /**
   * List recommendation items base on the data received
   
   * @param items - An array of item JSON objects
   */
  
  // ***********************
  // Listing memes on recomended (maybe following) page(s)
  // ***********************
  
  function listMemes(items, feature) {
    var images = document.querySelector('#memes');
    console.log("hiiiii");
    console.log(images);
    console.log(items);
    console.log(feature);
    images.innerHTML = '';
    //to be developed
    //images.innerHTML = $create('div'); // clear current results
    for (var i = 0; i < items.length; i++) {
       addMemes(images, items[i], feature);
    }
  }
  
  function addMemes(memeList, meme, feature) {
    // create the <figure> tag and specify the id and class attributes
    var meme_id = meme.id;
    var caption = meme.caption;
    var author_id = meme.userId;
    var image_url = meme.image_url;
    var followed = meme.follow;
    var favorite= meme.favorite;
    var numberOfLikes = meme.numberOfLikes;
    console.log(memeList);
    
    var figure = $create('figure',{
      id: 'meme-' + meme_id,
      className: 'meme'
    });
    
    figure.dataset.author = author_id;
    figure.dataset.meme_id = meme_id;
    figure.dataset.favorite = favorite;

    // item image
    if (image_url) {
      figure.appendChild($create('img', {src: image_url}));
    } else {
      figure.appendChild($create('img', {
        src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
      })); 
    }
    
    // follow user link
    var followUserLink = $create('i', {
      className: followed ? 'fas fa-user-friends '+ author_id : 'fa fa-user-plus '+ author_id
    });
    followUserLink.dataset.followed= followed;
    followUserLink.onclick = function(){changeFriendship(author_id)};

    //favorite link
    var favLink = $create('i', {
      id: 'fav-icon-' + meme_id,
      className: favorite ? 'fa fa-heart' : 'fa fa-heart-o'
    });
    favLink.innerHTML = numberOfLikes;
    favLink.onclick = function(){changeFavoriteItem(meme_id)};
    
    //figure
    var author = $create('figcaption');
    var user = $create('i');
    user.className = "fa fa-user"; 
    user.innerHTML = author_id;
    user.onclick = function(){loadProfile(user.innerHTML)};
    author.appendChild(user);
    if(feature == "recommend"){author.appendChild(followUserLink);}
    var figcaption = $create('figcaption');
    figcaption.innerHTML = caption;
    if (feature != "create"){figcaption.appendChild(favLink);}
    figure.appendChild(author);
    figure.appendChild(figcaption);
    
    
    memeList.appendChild(figure);
  }
   
  function createMemes(textValues, templateName){
        // request parameters
    console.log("create");
    var templateId = templateName;
    var category = textValues[2];
    var caption = textValues[3];
    var upText = textValues[0];
    var downText = textValues[1];
       
    console.log()
    var url = './create';
    var req = JSON.stringify({
        templateId : templateId, 
        category : category,
        caption: caption, 
        upText: upText, 
        downText: downText});
    console.log(req);
    ajax('POST', url, req,
      // successful callback
       function(res) {
         var result = JSON.parse(res);
         if (result.status === 'OK' || result.result === 'SUCCESS') {
           listMemes(result, "create")
         }
       });
    
  }
  init();

})();

