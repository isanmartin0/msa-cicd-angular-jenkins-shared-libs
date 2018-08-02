#!/usr/bin/env groovy

/**
 * Sets the display name of the build (for Java projects)
 */

def call() {

  def packageJSON = readJSON file: 'package.json'
  def v = packageJSON.version

  currentBuild.displayName = "${env.BRANCH_NAME}-${v}-${env.BUILD_NUMBER}"
      
}