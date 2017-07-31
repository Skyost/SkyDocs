$(document).ready(function() {
	// HANDLING TOP NAV BAR :
	$('#navbar ul li ul').each(function(){
		$(this).replaceWith('<div>' + $(this).html() + '</div>');
	});
	$('#navbar ul li div li').each(function(){
		$(this).replaceWith($(this).html());
	});
	
	$('#navbar ul li div').addClass('dropdown-menu');
	$('#navbar ul li div a').addClass('dropdown-item');
	
	var currentID = 1;
	$('#navbar ul li div').each(function() {
		$(this).attr('aria-labelledby', 'navbarDropdownMenuLink' + currentID);
		var parent = $(this).parent();
		parent.addClass('dropdown')
		parent.find('> a').addClass('dropdown-toggle').attr('id', 'navbarDropdownMenuLink' + (currentID++)).attr('data-toggle', 'dropdown').attr('aria-haspopup', 'true').attr('aria-expanded', 'false');
	});
	
	$('#navbar ul').addClass('navbar-nav mr-auto');
	$('#navbar ul li').addClass('nav-item');
	$('#navbar ul li > a').addClass('nav-link');
	
	// ANCHORS LINKS AND PRINT LINKS :
	anchors.options.placement = 'left';
	anchors.add('h1, h2, h3, h4');
	
	$('#content h1').each(function() {
		$(this).html($(this).html() + '<span class="print no-print"></span>');
	});
	
	$('.print').click(function() {
		$('article').print();
	});
	
	if(window.location.hash.length > 0) {
		goToHash(undefined, window.location.hash);
	}
	
	$('a[href*=\\#]').on('click', function(event) {
		goToHash(event, this.hash);
	});
	
	// TABLE OF CONTENTS :
	tocbot.init({
		tocSelector: '#nav-toc',
		contentSelector: '#content > article',
		headingSelector: 'h1, h2, h3'
	});
	
	var navBar = $('header nav');
	var navigation = $('#nav-toc');
	navigation.css('max-width', navigation.width());
	
	var marginPadding = parseInt(navigation.css('margin-top')) + parseInt(navigation.css('padding-top'));
	$(window).scroll(function() {
		if(navigation.height() + (marginPadding * 2) > $(window).height() || $(window).width() < 768) {
			resetPosition(navigation);
			return;
		}
		if(navBar.is(':in-viewport')) {
			resetPosition(navigation);
		}
		else {
			navigation.css('position', 'fixed');
		}
	});
	
	$(window).resize(function() {
		if(navBar.is(':in-viewport')) {
			navigation.css('max-width', '');
			navigation.css('max-width', navigation.width());
		}
		if(navigation.height() + (marginPadding * 2) > $(window).height() || $(window).width() < 768) {
			resetPosition(navigation);
		}
	});
	
	// OTHERS :
	$('table').each(function() {
		if(!$(this).hasClass('.table')) {
			$(this).addClass('table table-bordered table-responsive');
		}
	});
});

/**
* Found here : https://stackoverflow.com/a/18365991/3608831
*/

function goToHash(event, hash) {
	if(hash.length == 0) {
		return;
	}
	var jqueryHash = $(hash);
	if(!jqueryHash.length) {
		return;
	}
	if(!typeof event === 'undefined') {
		event.preventDefault();
	}
	$('html, body').animate({
		scrollTop: jqueryHash.offset().top
	}, 500);
	if(history.pushState) {
		history.pushState(null, null, hash);
	}
	else {
		location.hash = hash;
	}
}

function resetPosition(navigation) {
	navigation.css('position', '');
}