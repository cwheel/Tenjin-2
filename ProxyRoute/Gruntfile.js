module.exports = function(grunt) {
  grunt.initConfig({
    nodemon: {
    	dev: {
    		script: 'proxy.js'
    	}   
    }
  });
  
  grunt.loadNpmTasks('grunt-nodemon');  

  grunt.registerTask('default', ['nodemon']);
};