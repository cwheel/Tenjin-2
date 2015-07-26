module.exports = function(grunt) {
  grunt.initConfig({
    sass: {                              
      dist: {                            
        options: {                       
          style: 'expanded'
        },
        files: [                        
          {
            expand: true,
            cwd: "app/scss",
            src: ["**/*.scss"],
            dest: "app/css",
            ext: ".css"
          }
        ]
      }
    },
    express: {
      dev: {
        options: {
          script: 'server.js'
        }
      }
    },
    watch: {
      sass: {
          files: ['**/*.scss', '**/*.html'],
          tasks: ['sass'],
          options: {
               livereload: true,
          },
      },
      reload: {
          files: ['**/*.html'],
          tasks: ['sass'],
          options: {
               livereload: true,
          },
        }  
    }
  });

  grunt.loadNpmTasks('grunt-contrib-sass');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-express-server');  

  grunt.registerTask('default', ['express:dev', 'watch']);
};