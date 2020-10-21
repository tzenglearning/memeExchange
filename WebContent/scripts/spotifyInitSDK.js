/**
 * 
 */

window.player;

window.onSpotifyWebPlaybackSDKReady = () => {
	  const token = 'BQB-XhB5ra2uXZHOlOcs6J4fd76UoQ-z4SALettymuNxp6FRq_LrvwpPVjcwenJa0Jof2qBTnHQXiyRXtMcnpwKvu4wN2KfDtwjKN0loDWXYTSZIOtjPB192WpTIHE1wN2K0qhxKTy0Wd9OE4OPHTnOGSF_LzpGXCA';
	  player = new Spotify.Player({
	    name: 'Web Player',
	    getOAuthToken: cb => { cb(token); }
	  });
	
	  // Error handling
	  player.addListener('initialization_error', ({ message }) => { console.error(message); });
	  player.addListener('authentication_error', ({ message }) => { console.error(message); });
	  player.addListener('account_error', ({ message }) => { console.error(message); });
	  player.addListener('playback_error', ({ message }) => { console.error(message); });
	
	  // Playback status updates
	  player.addListener('player_state_changed', state => { console.log(state); });
	
	  // Ready
	  player.addListener('ready', ({ device_id }) => {
	    console.log('Ready with Device ID', device_id);
	  });
	
	  // Not Ready
	  player.addListener('not_ready', ({ device_id }) => {
	    console.log('Device ID has gone offline', device_id);
	  });
	
	  // Connect to the player!
	  player.connect();
	  
	 
};

function getState(){
	 window.player.getCurrentState().then(state => { 
		if(!state) {
			console.log('User is not playing music through the Web Playback SDK');
			return;
		}
		
		let {
			current_track,
			next_tracks: [next_track]
		} = state.track_window;
		console.log('Currently Playing', current_track);
 		console.log('Playing Next', next_track);
	});
}
