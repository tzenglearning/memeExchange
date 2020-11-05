(function() {

  /**
   * Variables
   */
  var user_id = '1111';
  var user_fullname = 'John';
  var lng = -122.08;
  var lat = 37.38;
  
  var mock_recommend_data = [{"url": "//storage.googleapis.com/meme_generator/mememasterCS701.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/blb.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/boat.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/sohappy.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/fine.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/hipster.png", 
                "caption": "Hello EveryOne","author":"Ray"},{"url": "//storage.googleapis.com/meme_generator/interesting.png", 
                "caption": "Hello EveryOne","author":"Ray"}]

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
    validateSession();
     //onSessionValid({"user_id":"1111","name":"John Smith","status":"OK"});
    listMemes(mock_recommend_data, "recommend")
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
    var avatar = document.querySelector('#avatar');
    var welcomeMsg = document.querySelector('#welcome-msg');
    var logoutBtn = document.querySelector('#logout-link');

    welcomeMsg.innerHTML = 'Welcome, ' + user_fullname;

    showElement(memes);
    showElement(avatar);
    showElement(welcomeMsg);
    showElement(logoutBtn, 'inline-block');
    hideElement(loginForm);
    hideElement(registerForm);
  }

  function onSessionInvalid() {
    var loginForm = document.querySelector('#login-form');
    var registerForm = document.querySelector('#register-form');
    var memes = document.querySelector('#memes');
    var avatar = document.querySelector('#avatar');
    var welcomeMsg = document.querySelector('#welcome-msg');
    var logoutBtn = document.querySelector('#logout-link');

    hideElement(memes);
    hideElement(avatar);
    hideElement(logoutBtn);
    hideElement(welcomeMsg);
    hideElement(registerForm);

    clearLoginError();
    showElement(loginForm);
  }

  function hideElement(element) {
    element.style.display = 'none';
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

    hideElement(memes);
    hideElement(avatar);
    hideElement(logoutBtn);
    hideElement(welcomeMsg);
    hideElement(loginForm);
    
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
    	return
    }
    
    if (username.match(/^[a-z0-9_]+$/) === null) {
    	showRegisterResult('Invalid username');
    	return
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

    xhr.open(method, url, true);

    xhr.onload = function() {
      if (xhr.status === 200) {
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
    console.log('loadTemplates');
    //activeBtn('create-btn');

    // // The request parameters
    // var url = './templates?page=1&var=2';
    // var data = null;

    // display loading message
   // showLoadingMessage('Loading ...');

    // make AJAX call
    // ajax('GET', url,
    //   // successful callback
    //   function(res) {
    //     var items =["storage.googleapis.com/meme_generator/drake"] //JSON.parse(res);
    //     if (!items || items.length === 0) {
    //       showWarningMessage('No templates.');
    //     } else {
    //       listItems(items);
    //     }
    //   },
    //   // failed callback
    //   function() {
    //     showErrorMessage('Cannot load templates.');
    //   }
    // );
    listTemplates(["//storage.googleapis.com/meme_generator/buzz.png"])
      document.querySelector('#create').addEventListener('click', createMemes);
  }

  /**
   * API #2 Load favorite (or visited) items API end point: [GET]
   * /history?user_id=1111
   */
  function loadFollowingItems() {
    activeBtn('following-btn');
    console.log("Load Following Items");
    // request parameters
    var url = './follow';
    var params = 'user_id=' + user_id;
    var req = JSON.stringify({});

    // display loading message
    //showLoadingMessage('Loading Following items...');

    // make AJAX call
  //   ajax('GET', url + '?' + params, req, function(res) {
  //     var items = JSON.parse(res);
  //     if (!items || items.length === 0) {
  //       showWarningMessage('No favorite item.');
  //     } else {
  //       listMemes(items);
  //     }
  //   }, function() {
  //     showErrorMessage('Cannot load Following items.');
  //   });
  // }
    listMemes([{"url": "//storage.googleapis.com/meme_generator/afraid.png", 
                 "caption": "Hello World", "author": "Charlie"}], "following")
 }
              
  /**
   * API #3 Load recommended items API end point: [GET]
   * /recommendation?user_id=1111
   */
  function loadRecommendedItems() {
    activeBtn('recommend-btn');
    console.log("Load Recommend Items")
    // request parameters
    //var url = './recommendation' + '?' + 'user_id=' + user_id + '&lat=' + lat + '&lon=' + lng;
    //var data = null;

    // display loading message
    //showLoadingMessage('Loading recommended items...');

    // make AJAX call
    // ajax('GET', url, data,
    //   // successful callback
    //   function(res) {
    //     var items = JSON.parse(res);
    //     if (!items || items.length === 0) {
    //       showWarningMessage('No recommended item. Make sure you have favorites.');
    //     } else {
    //       listItems(items);
    //     }
    //   },
    //   // failed callback
    //   function() {
    //     showErrorMessage('Cannot load recommended items.');
    //   }
    // );
    listMemes(mock_recommend_data, "recommend")
  }

  /**
   * API #4 Toggle favorite (or visited) items
   *
   * @param item_id - The item business id
   *
   * API end point: [POST]/[DELETE] /history request json data: {
   * user_id: 1111, visited: [a_list_of_business_ids] }
   */
  function changeFavoriteItem(item_id) {
    // check whether this item has been visited or not
    var li = document.querySelector('#item-' + item_id);
    var favIcon = document.querySelector('#fav-icon-' + item_id);
    var favorite = !(li.dataset.favorite === 'true');

    // request parameters
    var url = './history';
    var req = JSON.stringify({
      user_id: user_id,
      favorite: [item_id]
    });
    var method = favorite ? 'POST' : 'DELETE';

    ajax(method, url, req,
      // successful callback
      function(res) {
        var result = JSON.parse(res);
        if (result.status === 'OK' || result.result === 'SUCCESS') {
          li.dataset.favorite = favorite;
          favIcon.className = favorite ? 'fa fa-heart' : 'fa fa-heart-o';
        }
      });
  }

  // -------------------------------------
  // Create item list
  // -------------------------------------

  /**
   * List recommendation items base on the data received
   *
   * @param items - An array of item JSON objects
   */
  function listTemplates(items) {
    var images = document.querySelector('#memes');
    images.innerHTML = '';
    //to be developed
    //images.innerHTML = $create('div'); // clear current results
    for (var i = 0; i < items.length; i++) {
       addTemplate(images, items[i]);
    }
    images.innerHTML =  images.innerHTML + '<div> <input type ="text" placeholder ="upText" id = "upText" ></input> <input type ="text" placeholder = "downText" id = "downText" ></input><input type ="text" placeholder = "category" id = "category" ></input><input type ="text" placeholder = "caption" id = "caption" ></input><input type="submit" id = "create" value = "Create"></input> </div>';
    
  }

  /**
   * Add a single item to the list
   *
   * @param itemList - The <ul id="item-list"> tag (DOM container)
   * @param item - The item data (JSON object)
   *
  <div id="columns">
    <figure>
       <img src="//storage.googleapis.com/meme_generator/test.png">
	    <figcaption>Computer Science memes</figcaption>
	</figure>
   */
  function addTemplate(itemList, item) {
    // create the <figure> tag and specify the id and class attributes
    var figure = $create('figure');

    // item image
    if (item) {
      figure.appendChild($create('img', {src: item}));
    } else {
      figure.appendChild($create('img', {
        src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
      }));
    }
    itemList.appendChild(figure);
  }
  
  function listMemes(items, feature) {
    var images = document.querySelector('#memes');
    images.innerHTML = '';
    //to be developed
    //images.innerHTML = $create('div'); // clear current results
    for (var i = 0; i < items.length; i++) {
       addMemes(images, items[i], feature);
    }
  }
  
  function addMemes(itemList, item, feature) {
    // create the <figure> tag and specify the id and class attributes
    var figure = $create('figure');

    // item image
    if (item.url) {
      figure.appendChild($create('img', {src: item.url}));
    } else {
      figure.appendChild($create('img', {
        src: 'https://assets-cdn.github.com/images/modules/logos_page/GitHub-Mark.png'
      }));
    }
   
    //figure
    var author = $create('figcaption');
    author.innerHTML = '<i class="fa fa-user"></i>'+ item.author
    if(feature == "recommend"){author.innerHTML += '<i class="fa fa-user-plus add-user"> </i>';}
    var figcaption = $create('figcaption');
    figcaption.innerHTML = item.caption;
    if (feature != "create"){figcaption.innerHTML += '<i class="fa fa-heart fav">10</i>';}
    figure.appendChild(author);
    figure.appendChild(figcaption);
    
    
    itemList.appendChild(figure);
  }
   
  function createMemes(information){
        // request parameters
    console.log("create");
    var templateId = 'buzz';
    var category = document.querySelector('#category').value;
    var caption = document.querySelector('#caption').value;
    var upText = document.querySelector('#upText').value;
    var downText = document.querySelector('#downText').value;
       
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
           listMemes([{"url": result.image_url, 
                 "caption": caption, "author": result.user_id }], "create")
         }
       });
    
  }
  init();

})();
