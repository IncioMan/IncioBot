<!DOCTYPE html>
<html lang="en">
<head>
  <title>test</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
  <link href="https://fonts.googleapis.com/css?family=News+Cycle|Noto+sans" rel="stylesheet">
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="./scripts/fifa-website.js"></script>
  <link rel="stylesheet" href="./fifa-website.css">

<body>
	<div id="top-background">
	</div>
	<div class="" style="width: 100%; height: 100%; background: #5c7fb1; margin: 0px;">
		<div class="container-fluid container-head" style="width: 100%; height: 100%;">
		  <div class="header-div row">
			<div class="col-xs-4 col-md-8 logo-div">
				<img src="./fifaeuei.jpg" class="logo">
			</div>
			<div class="col-xs-8 col-md-4 icons-div">
				<span class="fa fa-info logo-white clickable" id="phone-logo-id"></span>
				<span class="fa fa-picture-o logo-white clickable" id="phone-logo-id"></span>
				<span class="fa fa-telegram logo-white clickable" id="telegram-logo-id"></span>
				<span class="fa fa-gitlab logo-white clickable" id="gitlab-logo-id"></span>
			</div>
			<div class="title-div">
				
			</div>
		  </div>
		  <div id="myCarousel" class="carousel slide text-center" data-ride="carousel">	  
			   <?php
			    $backend_address = getenv("INCIOBOT_BACKEND_ADDRESS");
				if(!$backend_address){
					fwrite(fopen('php://stdout', 'w'), "\nEnv var INCIOBOT_BACKEND_ADDRESS not found");
					$backend_address = "https://incio-bot.herokuapp.com/";
				}
				$json=file_get_contents($backend_address."fifa-api/matches/random/withcomments/simpleformat");
				fwrite(fopen('php://stdout', 'w'), "\nUsing as backend: ".$backend_address."fifa-api/matches/random/withcomments/simpleformat");
				$data = json_decode($json);

				$carousel_indicators;
				$carousel_items="";
				
				if (count($data)) {
					// Cycle through the array
					$i = 0;
					foreach ($data as $idx => $stand) {
						if($i++ === 0){
							$carousel_indicators.="<li data-target=\"#myCarousel\" data-slide-to=\"".($i-1)."\"class=\"active\"></li>\n";
							$carousel_items.="<div class=\"item active\">\n";
						}else{
							$carousel_indicators.="<li data-target=\"#myCarousel\" data-slide-to=\"".($i-1)."\"></li>\n";
							$carousel_items.="<div class=\"item\">\n";
						}
						$carousel_items.="<h2>";
						$carousel_items.="<span style=\"font-style:normal;\">$stand->result, $stand->name</span><br>\n";
						$carousel_items.="</h2>";
						if(strlen($stand->comment) > 90){
							$carousel_items.="<h2 class=\"comment-little\">";
						}else if (strlen($stand->comment) > 70){
							$carousel_items.="<h2 class=\"comment-medium\">";
						}else{
							$carousel_items.="<h2 class=\"comment\">";
						}
						$carousel_items.="<span style=\"font-style:italic;\">\"$stand->comment\"</span><br>\n";
						$carousel_items.="</h2>";
						$carousel_items.="<h4>";
						$carousel_items.="<span style=\"font-style:normal;\">$stand->date</span><br>\n";
						$carousel_items.="</h4>\n";
						$carousel_items.="</div>\n";
					}
					
					// Indicators	
					echo '<ol class="carousel-indicators">';
					echo $carousel_indicators;
					echo '</ol>';
					// Open the table
					echo "<div class=\"carousel-inner\" role=\"listbox\">\n";
					echo $carousel_items;
					// Close the table
					echo "</div>\n";
				}else{
					echo '<ol class="carousel-indicators">';
					echo '<li data-target="#myCarousel" data-slide-to="0" class="active"></li>';
					echo '</ol>';
					// Open the table
					echo "<div class=\"carousel-inner\" role=\"listbox\">";

					echo "<div class=\"item active\">";
					echo "<h2>";
					echo "<span style=\"font-style:normal;\">Nessuna partita da visualizzare</span><br>";
					echo "</h2>";
					echo "<h2 class=\"comment\">";
					echo "<span style=\"font-style:italic;\">\"Registra qualche risultato con commento per vederli visualizzati qui\"</span><br>";
					echo "</h2>";
					echo "<h4>";
					echo "<span style=\"font-style:normal;\"></span><br>";
					echo "</h4>";
					echo "</div>";

					// Close the table
					echo "</div>";
				}
			  ?>
			  
			  <!-- Left and right controls -->
			  <a class="left carousel-control" href="#myCarousel" role="button" data-slide="prev">
				<span class="glyphicon glyphicon-chevron-left" aria-hidden="true"></span>
				<span class="sr-only">Previous</span>
			  </a>
			  <a class="right carousel-control" href="#myCarousel" role="button" data-slide="next">
				<span class="glyphicon glyphicon-chevron-right" aria-hidden="true"></span>
				<span class="sr-only">Next</span>
			  </a>
			</div>
			<div class="arrow-div">
				<button id="down-button" type="button" class="btn btn-circle"><i class="fa fa-arrow-down"></i></button>
			</div>
		</div>
	</div>
	<div class="" style="width: 100%; background: white;" id="first">
		<div class="container-fluid container-head" style="width: 100%; height: 100%;">
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-6" style="height: 315px; background: orange">
				</div>
				<div class="col-xs-12 col-sm-12 col-md-6" style="height: 315px; background: yellow">
				</div>
		    </div>
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-6" style="height: 315px; background: yellow">
				</div>
				<div class="col-xs-12 col-sm-12 col-md-6 grid-block" style="height: 315px; background: orange">
					<video autoplay="autoplay" loop="loop" width="100%" height="100%" style="overlow: hidden">
						<source src="video.mp4" type="video/mp4" />
						<img src="video.gif" width="400" height="300" />
					</video>
				</div>
		    </div>
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-6" style="height: 315px; background: orange">
				</div>
				<div class="col-xs-12 col-sm-12 col-md-6 grid-block" style="height: 315px; background: yellow">
					<iframe width="100%" height="100%" src="https://www.youtube.com/embed/9DYLH6w6avs" frameborder="0" allowfullscreen>
					</iframe>
				</div>
		    </div>
			<div class="row">
				<div class="col-xs-12 col-sm-12 col-md-6" style="height: 315px; background: yellow">
				</div>
				<div class="col-xs-12 col-sm-12 col-md-6 grid-block" style="height: 315px; background: yellow">
					<iframe width="100%" height="100%" src="https://www.youtube.com/embed/A6EHIzWpi_E" frameborder="0" allowfullscreen>
					</iframe>
				</div>
		    </div>
		</div>
	</div>
</body>
</html>