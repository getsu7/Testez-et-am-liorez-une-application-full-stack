/**
 * @type {Cypress.PluginConfig}
 */
 import * as registerCodeCoverageTasks from '@cypress/code-coverage/task';

 const setupPlugins = (on: Cypress.PluginEvents, config: Cypress.PluginConfigOptions): Cypress.PluginConfigOptions => {
   registerCodeCoverageTasks.default(on, config);
   return config;
 };

 export default setupPlugins;

