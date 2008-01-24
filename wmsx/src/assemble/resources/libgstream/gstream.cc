#include <gstream.h>
#include <cstring>
#include <cstdlib>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <errno.h>
#include <streambuf>
#include <iostream>
#include <string>
#include <sstream>


namespace std
{


static string vo="";
static string dest="";
static string tmpdir="";
static bool debug=false;


void gstream_debug(const bool flag)
{
    debug=flag;
}


bool gstream_debug()
{
    return debug;
}


static void ginit()
{
    if ( getenv("LCG_CATALOG_TYPE")==NULL )
    {
	cerr << "[gstream] Environmental variable LCG_CATALOG_TYPE is not set!\n[gstream]\tvoid init()\n";
	exit(1);
    }
    if ( getenv("LFC_HOST")==NULL )
    {
	cerr << "[gstream] Environmental variable LFC_HOST is not set!\n[gstream]\tvoid init()\n";
	exit(1);
    }
    if ( getenv("LCG_GFAL_VO")==NULL )
    {
	cerr << "[gstream] Environmental variable LCG_GFAL_VO is not set!\n[gstream]\tvoid init()\n";
	exit(1);
    }
    if ( getenv("DEST")==NULL )
    {
	cerr << "[gstream] Environmental variable DEST is not set!\n[gstream]\tvoid init()\n";
	exit(1);
    }
    vo=getenv("LCG_GFAL_VO");
    dest=getenv("DEST");
    ostringstream command;
    int err;
    command << "grid-proxy-info -e >&/dev/null";
    err=system(command.str().c_str());
    command.str("");
    command.clear();
    if ( err!=0 )
    {
	cerr << "[gstream] No valid grid-proxy!\n[gstream]\tvoid init()\n";
	exit(1);
    }
    if ( getenv("TMPDIR")==NULL ) tmpdir=""; else tmpdir=getenv("TMPDIR");
    if ( tmpdir=="" )
    {
	string pwd;
	if ( getenv("PWD")==NULL ) pwd=""; else pwd=getenv("PWD");
	tmpdir=pwd;
    }
}


static void cleanup(const string &filename, const string &directoryname)
{
    ostringstream command;
    command << "rm -f " << filename;
    system(command.str().c_str());
    command.str("");
    command.clear();
    command << "rm -rf " << directoryname;
    system(command.str().c_str());
    command.str("");
    command.clear();
}


static void mkstagefile(const string &gfilename, string &filename, string &directoryname)
{
    string fname;
    istringstream iss(gfilename);
    while ( getline(iss, fname, '/') ) { }
    iss.str("");
    iss.clear();
    char uniquedirtag[]="gstream_XXXXXX";
    directoryname=tmpdir+"/"+uniquedirtag;
    char *uniquedir=new char[directoryname.length()+16];
    sprintf(uniquedir, "%s", directoryname.c_str());
    if ( mkdtemp(uniquedir)==NULL )
    {
	cerr << "[gstream] Could not create temporary directory!\n[gstream]\tvoid mkstagefile(const string&, string&, string&)\n";
	exit(1);
    }
    string dname;
    iss.str(uniquedir);
    while ( getline(iss, dname, '/') ) { }
    iss.str("");
    iss.clear();
    delete[] uniquedir;
    directoryname=tmpdir+"/"+dname;
    filename=directoryname+"/"+fname;
}


static bool gexists(const string &gfilename)
{
    ostringstream command;
    command << "lcg-lg --vo " << vo << " ";
    command << "lfn:" << gfilename << " >&/dev/null";
    int err=system(command.str().c_str());
    command.str("");
    command.clear();
    return (err==0);
}


static void gget(const string &gfilename, const string &filename, const string &directoryname)
{
    if ( debug ) cerr << "[gstream] Getting: " << gfilename << endl;
    ostringstream command;
    command << "lcg-cp --vo " << vo << " ";
    command << "lfn:" << gfilename << " file:" << filename;
    int err=1;
    int tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error getting file " << gfilename << " . Giving up after the 5-th try!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	    cleanup(filename, directoryname);
	    exit(1);
	}
    }
    command.str("");
    command.clear();
}


static void gdel(const string &gfilename)
{
    if ( debug ) cerr << "[gstream] Deleting: " << gfilename << endl;
    ostringstream command;
    command << "lcg-del --vo " << vo << " ";
    command << "-a ";
    command << "lfn:" << gfilename;
    int err=1;
    int tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error deleting file " << gfilename << " . Giving up after the 5-th try!\n[gstream]\tvoid gdel(const string&)\n";
	    exit(1);
	}
    }
    command.str("");
    command.clear();
}


static void gput(const string &gfilename, const string &filename, const string &directoryname)
{
    if ( debug ) cerr << "[gstream] Putting: " << gfilename << endl;
    ostringstream command;
    command << "lcg-cr --vo " << vo << " ";
    command << "-d " << dest << " ";
    command << "-l lfn:" << gfilename << " file:" << filename << " 1>/dev/null";
    int err=1;
    int tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error putting file " << gfilename << " . Giving up after the 5-th try!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	    cleanup(filename, directoryname);
	    exit(1);
	}
    }
    command.str("");
    command.clear();
    command << "lfc-chmod 664 " << gfilename;
    err=1;
    tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error in lfc-chmod " << gfilename << " . Giving up after the 5-th try!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	    cleanup(filename, directoryname);
	    exit(1);
	}
    }
    command.str("");
    command.clear();
}


gstream::gstream()
 : opened_(false)
{
}


gstream::gstream(const string &name, ios::openmode mode)
 : opened_(false)
{
    gstream::open(name, mode);
}


gstream::~gstream()
{
    gstream::close();
}


gstream& gstream::open(const string &name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    ginit();
	    mkstagefile(gfilename_, filename_, directoryname_);
	    if ( gexists(gfilename_) ) gget(gfilename_, filename_, directoryname_);
	}
	else 
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	fstream::open(filename_.c_str(), mode);
	opened_=true;
    }
    return *this;
}


gstream& gstream::close()
{
    if ( opened_==true )
    {
	fstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    if ( gexists(gfilename_) ) gdel(gfilename_);
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
    }
    return *this;
}


igstream::igstream()
 : opened_(false)
{
}


igstream::igstream(const string &name, ios::openmode mode)
 : opened_(false)
{
    igstream::open(name, mode);
}


igstream::~igstream()
{
    igstream::close();
}


igstream& igstream::open(const string &name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    ginit();
	    if ( gexists(gfilename_) )
	    {
		mkstagefile(gfilename_, filename_, directoryname_);
		gget(gfilename_, filename_, directoryname_);
	    }
	    else
	    {
		cerr << "[gstream] File " << gfilename_ << " does not exist.\n[gstream]\tigstream& igstream::open(const string&, ios::openmode)\n";
		exit(1);
	    }
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	ifstream::open(filename_.c_str(), mode);
	opened_=true;
    }
    return *this;
}


igstream& igstream::close()
{
    if ( opened_==true )
    {
	ifstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" ) cleanup(filename_, directoryname_);
	opened_=false;
    }
    return *this;
}


ogstream::ogstream()
 : opened_(false)
{
}


ogstream::ogstream(const string &name, ios::openmode mode)
 : opened_(false)
{
    ogstream::open(name, mode);
}


ogstream::~ogstream()
{
    ogstream::close();
}


ogstream& ogstream::open(const string &name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    ginit();
	    mkstagefile(gfilename_, filename_, directoryname_);
	    if ( (mode&ios::app!=0) && gexists(gfilename_) )
		gget(gfilename_, filename_, directoryname_);
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	ofstream::open(filename_.c_str(), mode);
	opened_=true;
    }
    return *this;
}


ogstream& ogstream::close()
{
    if ( opened_==true )
    {
	ofstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    if ( gexists(gfilename_) ) gdel(gfilename_);
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
    }
    return *this;
}



// ================= gpstream stuff ====================================


static void searchandreplace(string &str, const string &f, const string &r)
{
    unsigned int pos=str.find(f);
    while ( pos<str.length() ) { str.replace(pos, f.length(), r); pos=str.find(f); }
}


igpstream::igpstream()
 : opened_(false)
{
}


igpstream::igpstream(const string &name, const string &pipe)
 : opened_(false)
{
    igpstream::open(name, pipe);
}


igpstream::~igpstream()
{
    igpstream::close();
}


igpstream& igpstream::open(const string &name, const string &pipe)
{
    if ( opened_==false )
    {
	gfilename_=name;
	pipename_=pipe;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    ginit();
	    if ( gexists(gfilename_) )
	    {
		mkstagefile(gfilename_, filename_, directoryname_);
		gget(gfilename_, filename_, directoryname_);
	    }
	    else
	    {
		cerr << "[gstream] File " << gfilename_ << " does not exist.\n[gstream]\tigpstream& igpstream::open(const string&, ios::openmode)\n";
		exit(1);
	    }
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	searchandreplace(pipename_, "%f", filename_);
	ipstream::open(pipename_);
	opened_=true;
    }
    return *this;
}


igpstream& igpstream::close()
{
    if ( opened_==true )
    {
	ipstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" ) cleanup(filename_, directoryname_);
	opened_=false;
    }
    return *this;
}


ogpstream::ogpstream()
 : opened_(false)
{
}


ogpstream::ogpstream(const string &name, const string &pipe)
 : opened_(false)
{
    ogpstream::open(name, pipe);
}


ogpstream::~ogpstream()
{
    ogpstream::close();
}


ogpstream& ogpstream::open(const string &name, const string &pipe)
{
    if ( opened_==false )
    {
	gfilename_=name;
	pipename_=pipe;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    ginit();
	    mkstagefile(gfilename_, filename_, directoryname_);
	    if ( gexists(gfilename_) )
		gget(gfilename_, filename_, directoryname_);
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	searchandreplace(pipename_, "%f", filename_);
	opstream::open(pipename_);
	opened_=true;
    }
    return *this;
}


ogpstream& ogpstream::close()
{
    if ( opened_==true )
    {
	opstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    if ( gexists(gfilename_) ) gdel(gfilename_);
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
    }
    return *this;
}


}

