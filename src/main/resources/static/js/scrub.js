//This is our polling function. It's going to make calls to the
//server every 5 seconds to check on the status of the scrubbing
//It will delete the html in the 'progressDiv' in scrub.html and
//replace it with the code from progress.html
function doPoll(){
    //Select the progress div
    //Call jQuery empty() function to delete the html in that div
    //then use jQuery's load() function to load the progress.html fragment
    //Finally, define a callback function that runs when the laod is complete
    $('.progressDiv').empty().load('/scrub/start.status', function(){
    
    	//There is a invisible textbox in progress.html line 14
        //This line reads the number in that textbox
        var value = $('.progressTxt').val();
        
        //Now, we need to make the textbox invisible. We have to do this
        //after read the value from the textbox.
        //To do this we will select all items on the page with the 'invisible'
        //css class and call jQuery's hide function on it
        $('.invisible').hide();
        
        if(value < 100 || value == undefined){
        	//At this point value is less than 100% or it's undefined
            //If it's either we are going to wait 5 seconds and then
            //call this function recursively again.  We will do that
            //using the browser's setTimeout function, which takes a 
            //funciton and the delay time in milliseconds.
            setTimeout(doPoll, 5000);
        } else {
        	//If we are here, then we need to cancel the recursion
            //so all we are going to do is load the progress.html fragment
            //and then function will terminate
            $('.progressDiv').empty().load('/scrub/start.status');
        }
    });
}

//Although this isn't technically correct, the $(document).ready can be
//thought of as sort of a main() method for jquery.

//In reality, this is an event fired by the document object when the page
//is ready to get displayed to the user
$(document).ready(function () {

	//We are going to attach an event handler to the submit event
    //on the scrubForm (the form is the default html in 'progressDiv' on scrub.html)
    $('#scrubForm').submit(function(e){
    	//Prevent the default action. For a form, this is a full page
        //submit to the server
        e.preventDefault();
	
		//Get the cross site forgery information to prevent 403 forbidden
        var token = $("meta[name='_csrf']").attr('content');
        var header = $("meta[name='_csrf_header']").attr('content');
	
		//Send the cross site forgery information to the server
        $.ajaxSetup({
            beforeSend : function(xhr, settings){
                xhr.setRequestHeader(header, token);
            }
        });
		
		//Now, we are going to do an ajax post the server to kick off
        //the scrubbing operation. When this post is complete, 
        //we will call our doPoll function (above) to check the progress
        //of the scrubbing
        $.post('/scrub/start.begin', function () {
            doPoll();
        });
    });
});