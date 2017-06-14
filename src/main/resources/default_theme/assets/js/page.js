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
	
	// TABLE OF CONTENTS :
	$('#content nav').toc({
		'container': '#content article',
		'prefix': 'nav'
	});
	
	$(window).scroll(function() {
		if($('#content nav ul').height() > $(window).height()) {
			return;
		}
		if($(window).width() < 750) {
			$('#content nav ul').css('position', '');
			return;
		}
		$('#content nav ul').css('position', isElementInViewport($('header')) ? '' : 'fixed').css('top', '31px');
	});
	
	// ANCHORS LINKS :
	$('#content h1, #content h2, #content h3, #content h4').each(function() {
		$(this).html($(this).html() + '<a href="#' + $(this).attr('id') + '" class="anchor"><i class="fa fa-link" aria-hidden="true"></i></a>');
	});
	
	// OTHERS :
	$('table').each(function() {
		if(!$(this).hasClass('.table')) {
			$(this).addClass('table table-bordered table-responsive');
		}
	});
});

function isElementInViewport(el) {
	if (typeof jQuery === "function" && el instanceof jQuery) {
		el = el[0];
	}
	var rect = el.getBoundingClientRect();
	return (
		rect.top >= 0 &&
		rect.left >= 0 &&
		rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
		rect.right <= (window.innerWidth || document.documentElement.clientWidth)
    );
}