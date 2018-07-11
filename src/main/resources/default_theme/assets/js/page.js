$(document).ready(function() {	
	// ANCHORS LINKS AND PRINT LINKS :
	anchors.add('article h1, article h2, article h3, article h4');
	
	// SYNTAX HIGHLIGHTING :
	if(("code[class^='language-']").length) {
		$('head').append('<link rel="stylesheet" type="text/css" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/styles/solarized-dark.min.css"/>');
		$.getScript('https://cdnjs.cloudflare.com/ajax/libs/highlight.js/9.12.0/highlight.min.js', function() {
			$('pre code').each(function(i, block) {
				hljs.highlightBlock(block);
			});
		});
	}
	
	// OTHERS :	
	$('table').each(function() {
		$(this).replaceWith('<div class="table-responsive"><table class="table table-bordered">' + this.innerHTML + '</table></div>');
	});
	
	$('#mobile-bar .fa').click(function() {
		$('#navigation').toggleClass('d-none');
	});
});

function localize(messages) {
	for(var selector in messages){
		var target = selector.split('&');
		switch(target[1].toLowerCase()) {
			case 'html':
			$(target[0]).html(messages[selector]);
			break;
			default:
			$(target[0]).attr(target[1], messages[selector]);
			break;
		}
	}
}