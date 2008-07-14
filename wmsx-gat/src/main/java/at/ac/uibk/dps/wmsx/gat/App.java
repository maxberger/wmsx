package at.ac.uibk.dps.wmsx.gat;

//~--- non-JDK imports --------------------------------------------------------
import org.gridlab.gat.GAT;
import org.gridlab.gat.GATContext;
import org.gridlab.gat.Preferences;
import org.gridlab.gat.URI;
import org.gridlab.gat.io.File;
import org.gridlab.gat.resources.HardwareResourceDescription;
import org.gridlab.gat.resources.Job;
import org.gridlab.gat.resources.JobDescription;
import org.gridlab.gat.resources.ResourceBroker;
import org.gridlab.gat.resources.ResourceDescription;
import org.gridlab.gat.resources.SoftwareDescription;
import org.gridlab.gat.security.CertificateSecurityContext;

//~--- JDK imports ------------------------------------------------------------

import java.net.URISyntaxException;

import java.util.Hashtable;
import java.util.Map;
import java.util.*;
import java.io.*;

public class App {

    public static void main(String[] args) throws URISyntaxException {
        System.out.println("Hello World!");

        File fileout = null;
        File fileerr = null;
        File input1 = null;
        File input2 = null;
        File execScript = null;
        File output1 = null;
        ResourceBroker broker = null;
        ResourceDescription hwrDescription = null;
        SoftwareDescription swDescription = null;
        JobDescription jobDescription = null;
        Job job = null;
        GATContext context = new GATContext();
        Preferences globalPrefs = new Preferences();

        globalPrefs.put("ResourceBroker.adaptor.name", "Glite");
        globalPrefs.put("AdvertService.adaptor.name", "Glite");

        // e.g. /home/tom/workspace/thomas/lib/adaptors
        System.setProperty("gat.adaptor.path", GliteTestsConstants.GAT_ADAPTOR_PATH);

        // System.setProperty("gat.debug", "true");
        System.setProperty("gat.verbose", "true");

        /** ************************************************* */
        /** Information necessary for VOMS proxy creation ** */
        /** ************************************************* *///      globalPrefs.put("VirtualOrganisation", "compchem");
//      globalPrefs.put("vomsServerURL", "voms.cnaf.infn.it");
//      globalPrefs.put("vomsServerPort", "15003");
//      globalPrefs.put("vomsHostDN", "/C=IT/O=INFN/OU=Host/L=CNAF/CN=voms.cnaf.infn.it");
        globalPrefs.put("VirtualOrganisation", "voce");
        globalPrefs.put("vomsServerURL", "skurut19.cesnet.cz");
        globalPrefs.put("vomsServerPort", "7001");
        globalPrefs.put("vomsHostDN", "/DC=cz/DC=cesnet-ca/O=CESNET/CN=skurut19.cesnet.cz");

        CertificateSecurityContext secContext =
                new CertificateSecurityContext(new URI(GliteTestsConstants.X509_KEY_PATH),
                new URI(GliteTestsConstants.X509_CERT_PATH),
                GliteTestsConstants.X509_KEY_PASSWORD);

        context.addSecurityContext(secContext);
        context.addPreferences(globalPrefs);

        try {
            execScript = GAT.createFile(context, new URI("file:/home/berger/input/twocat.sh"));
            fileout = GAT.createFile(context, new URI("jobsubmit_out.txt"));
            fileerr = GAT.createFile(context, new URI("jobsubmit_err.txt"));
            input1 = GAT.createFile(context, new URI("file:/home/berger/input/hello"));
            input2 = GAT.createFile(context, new URI("file:/home/berger/input/world"));
            output1 = GAT.createFile(context, new URI("output.txt"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

//          /                     broker = GAT.createResourceBroker(context, new URI("https://glite-rb-00.cnaf.infn.it:7443/glite_wms_wmproxy_server"));
            broker = GAT.createResourceBroker(context,
                    new URI("https://skurut67-6.cesnet.cz:7443/glite_wms_wmproxy_server"));
            swDescription = new SoftwareDescription();
            swDescription.setExecutable("/bin/sh");

            String[] arguments = {"twocat.sh", "hello", "world"};

            swDescription.setArguments(arguments);
            swDescription.setPreStaged(execScript);
            swDescription.addPreStagedFile(input1);
            swDescription.addPreStagedFile(input2);
            swDescription.setStdout(fileout);
            swDescription.setStderr(fileerr);
            swDescription.addPostStagedFile(output1);

            Hashtable<String, Object> hwrAttrib = new Hashtable<String, Object>();

            hwrAttrib.put("memory.size", 1.0f);
            hwrDescription = new HardwareResourceDescription(hwrAttrib);
            jobDescription = new JobDescription(swDescription, hwrDescription);
        } catch (Exception e) {
            System.err.println("Could not create description: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        try {
            job = broker.submitJob(jobDescription);
        } catch (Exception e) {
            System.err.println("Could not submit job: " + e);
            System.exit(1);
        }

        while (true) {
            try {
                Map info = job.getInfo();

                System.err.print("job info: ");
                System.err.println(info);

                int istate = job.getState();

                System.out.println("istate " + istate);

                if (istate == Job.STOPPED) {
                    break;
                }

                if (istate == Job.SUBMISSION_ERROR) {
                    break;
                }

                Thread.sleep(10000);
            } catch (Exception e) {
                System.err.println("getInfo failed: " + e);
                e.printStackTrace();

                break;
            }
        }
    }
}


//~ Formatted by Jindent --- http://www.jindent.com
