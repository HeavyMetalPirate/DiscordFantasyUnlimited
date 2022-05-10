
export function convert_color(c){
   var color;
   if(c.indexOf('rgb') == -1){
      color = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(c);
      color = color ? [
        parseInt(color[1], 16),
        parseInt(color[2], 16),
        parseInt(color[3], 16)
      ] : [255,255,255];
   } else {
      color = c.split('(')[1].split(')')[0].split(',');
      for(var i = 0; i < color.length; i++){
         color[i] = parseInt(color[i]);
      }
   }
   return color;
};

export function get_steps(c1, c2, st){
   c1 = convert_color(c1);
   c2 = convert_color(c2);
   var s_r = Math.floor((c1[0] - c2[0]) / st);
   var s_g = Math.floor((c1[1] - c2[1]) / st);
   var s_b = Math.floor((c1[2] - c2[2]) / st);
   var steps = {};
   var cth = function(c) {
      var h = c.toString(16);
      return h.length == 1 ? "0" + h : h;
   };
   var toHEX = function(v){
      return "#" + cth(v[0]) + cth(v[1]) + cth(v[2]);
   };
   var toRGB = function(v){
      return 'rgb(' + v.join(',') +  ')';
   };
   steps[toRGB(c1)] = {
      hex : toHEX(c1).toUpperCase(),
      rgb : toRGB(c1)
   };
   for(var i = 0; i < st; i++){
      if((c1[0] - s_r) > 0) c1[0] -= s_r;
      if((c1[1] - s_g) > 0) c1[1] -= s_g;
      if((c1[2] - s_b) > 0) c1[2] -= s_b;
      c1[0] = (c1[0] > 255) ? 255 : c1[0];
      c1[1] = (c1[1] > 255) ? 255 : c1[1];
      c1[2] = (c1[2] > 255) ? 255 : c1[2];
      if(!steps[toRGB(c1)]) steps[toRGB(c1)] = {
         hex : toHEX(c1).toUpperCase(),
         rgb : toRGB(c1)
      };
   }
   return steps;
};

export function calculateHealthPercentage(character){
    var p = (character.resources.currentHealth / character.resources.maxHealth) * 100;
    return p;
};

export function calculateResourcePercentage(character){
    var p = (character.resources.currentResource / character.resources.maxResource) * 100;
    return p;
};