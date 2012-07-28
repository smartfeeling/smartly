** HTDOCS folder. **

This folder contains files that will be deployed to htdocs root folder of your web server.
Put here your templates and resource files (i.e. images) to use in CMS "page" module.

** DEPLOYERS **

In production environment you should declare a deployer into your Smartly package class overriding the load method.
i.e.
    @Override
    public void load() {
        Smartly.register(new Deployer(Smartly.getConfigurationPath()));
    }

Deployers must extend HtmlDeployer:

    public class QRHtmlDeployer extends HtmlDeployer {
        public QRHtmlDeployer() {
            super.setOverwrite(true); // default is false.
        }
    }