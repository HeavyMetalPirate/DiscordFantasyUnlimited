import * as FantasyUnlimited from "../../types/rest-entities";

export function convert_color(c: string){
   let color;
   let colorValue: number[] = [];
   if(c.indexOf('rgb') == -1){
      color = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(c);
      colorValue = color ? [
        parseInt(color[1], 16),
        parseInt(color[2], 16),
        parseInt(color[3], 16)
      ] : [255,255,255];
   } else {
      color = c.split('(')[1].split(')')[0].split(',');
      for(var i = 0; i < color.length; i++){
         colorValue[i] = parseInt(color[i]);
      }
   }
   return colorValue;
};

type StepType = {
    hex: string;
    rgb: string;
}

export function get_steps(c1_string:string, c2_string: string, st: number){
   const c1 = convert_color(c1_string);
   const c2 = convert_color(c2_string);
   const s_r = Math.floor((c1[0] - c2[0]) / st);
   const s_g = Math.floor((c1[1] - c2[1]) / st);
   const s_b = Math.floor((c1[2] - c2[2]) / st);
   const steps: { [id: string]: StepType } = {};
   const cth = function (c: number) {
      const h = c.toString(16);
      return h.length == 1 ? "0" + h : h;
   };
   const toHEX = function (v: number[]) {
      return "#" + cth(v[0]) + cth(v[1]) + cth(v[2]);
   };
   const toRGB = function (v: number[]) {
      return 'rgb(' + v.join(',') + ')';
   };
   steps[toRGB(c1)] = {
      hex : toHEX(c1).toUpperCase(),
      rgb : toRGB(c1)
   };
   for(let i = 0; i < st; i++){
      if((c1[0] - s_r) > 0) c1[0] -= s_r;
      if((c1[1] - s_g) > 0) c1[1] -= s_g;
      if((c1[2] - s_b) > 0) c1[2] -= s_b;
      c1[0] = (c1[0] > 255) ? 255 : c1[0];
      c1[1] = (c1[1] > 255) ? 255 : c1[1];
      c1[2] = (c1[2] > 255) ? 255 : c1[2];
      if(!steps[toRGB(c1)]) {
          steps[toRGB(c1)] = {
             hex : toHEX(c1).toUpperCase(),
             rgb : toRGB(c1)
          };
      }
   }
   return steps;
};

export function caclulcateExperiencePercentage(currentExperience: number, nextLevelExperience: number) {
   return (currentExperience / nextLevelExperience) * 100;
}

export function calculateHealthPercentage({resources}: FantasyUnlimited.REST.PlayerCharacterItem){
   return (resources.currentHealth / resources.maxHealth) * 100;
};

export function calculateResourcePercentage({resources}: FantasyUnlimited.REST.PlayerCharacterItem){
   return (resources.currentResource / resources.maxResource) * 100;
};