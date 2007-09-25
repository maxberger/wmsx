#include "gstream.h"


namespace std
{


void cleanup(const string &filename, const string &directoryname)
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


void mkstagefile(const string &gfilename, string &filename, string &directoryname)
{
    string tmpdir;
    if ( getenv("TMPDIR")==NULL ) tmpdir=""; else tmpdir=getenv("TMPDIR");
    if ( tmpdir=="" )
    {
	string pwd;
	if ( getenv("PWD")==NULL ) pwd=""; else pwd=getenv("PWD");
	tmpdir=pwd;
    }
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


void gget(const string &gfilename, const string &filename, const string &directoryname)
{
    if ( getenv("LCG_CATALOG_TYPE")==NULL )
    {
	cerr << "[gstream] Environmental variable LCG_CATALOG_TYPE is not set!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    if ( getenv("LFC_HOST")==NULL )
    {
	cerr << "[gstream] Environmental variable LFC_HOST is not set!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    string vo;
    if ( getenv("LCG_GFAL_VO")==NULL ) vo=""; else vo=getenv("LCG_GFAL_VO");
    if ( vo=="" )
    {
	cerr << "[gstream] Environmental variable LCG_GFAL_VO is not set!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    ostringstream command;
    int err;
    command << "grid-proxy-info -e >&/dev/null";
    err=system(command.str().c_str());
    command.str("");
    command.clear();
    if ( err!=0 )
    {
	cerr << "[gstream] No valid grid-proxy!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    cerr << "[gstream] Getting: " << gfilename << endl;
    command << "lcg-cp --vo " << vo << " ";
    command << "lfn:" << gfilename << " file:" << filename;
    err=1;
    int tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error getting file. Giving up after the 5-th try!\n[gstream]\tvoid gget(const string&, const string&, const string&)\n";
	    cleanup(filename, directoryname);
	    exit(1);
	}
    }
    command.str("");
    command.clear();
}


void gput(const string &gfilename, const string &filename, const string &directoryname)
{
    if ( getenv("LCG_CATALOG_TYPE")==NULL )
    {
	cerr << "[gstream] Environmental variable LCG_CATALOG_TYPE is not set!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    if ( getenv("LFC_HOST")==NULL )
    {
	cerr << "[gstream] Environmental variable LFC_HOST is not set!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    string vo;
    if ( getenv("LCG_GFAL_VO")==NULL ) vo=""; else vo=getenv("LCG_GFAL_VO");
    if ( vo=="" )
    {
	cerr << "[gstream] Environmental variable LCG_GFAL_VO is not set!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    ostringstream command;
    int err;
    command << "grid-proxy-info -e >&/dev/null";
    err=system(command.str().c_str());
    command.str("");
    command.clear();
    if ( err!=0 )
    {
	cerr << "[gstream] No valid grid-proxy!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    string dest;
    if ( getenv("DEST")==NULL ) dest=""; else dest=getenv("DEST");
    if ( dest=="" )
    {
	cerr << "[gstream] Environmental variable DEST is not set!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	cleanup(filename, directoryname);
	exit(1);
    }
    cerr << "[gstream] Putting: " << gfilename << endl;
    command << "lcg-lg --vo " << vo << " ";
    command << "lfn:" << gfilename << " >&/dev/null";
    err=system(command.str().c_str());
    command.str("");
    command.clear();
    int tries;
    if ( err==0 )
    {
	command << "lcg-del --vo " << vo << " ";
	command << "-s " << dest << " ";
	command << "lfn:" << gfilename;
	err=1;
	tries=0;
	while ( err!=0 )
	{
	    err=system(command.str().c_str());
	    if ( err!=0 ) sleep(60);
	    ++tries;
	    if ( tries>=5 )
	    {
		cerr << "[gstream] Error deleting file. Giving up after the 5-th try!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
		cleanup(filename, directoryname);
		exit(1);
	    }
	}
	command.str("");
	command.clear();
    }
    command << "lcg-cr --vo " << vo << " ";
    command << "-d " << dest << " ";
    command << "-l lfn:" << gfilename << " file:" << filename << " 1>/dev/null";
    err=1;
    tries=0;
    while ( err!=0 )
    {
	err=system(command.str().c_str());
	if ( err!=0 ) sleep(60);
	++tries;
	if ( tries>=5 )
	{
	    cerr << "[gstream] Error putting file. Giving up after the 5-th try!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
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
	    cerr << "[gstream] Error in lfc-chmod. Giving up after the 5-th try!\n[gstream]\tvoid gput(const string&, const string&, const string&)\n";
	    cleanup(filename, directoryname);
	    exit(1);
	}
    }
    command.str("");
    command.clear();
}


gstream::gstream()
 : opened_(false), closed_(true)
{
}


gstream::gstream(const char* name, ios::openmode mode)
 : opened_(false), closed_(true)
{
    gstream::open(name, mode);
}


gstream::~gstream()
{
    gstream::close();
}


gstream& gstream::open(const char* name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    mkstagefile(gfilename_, filename_, directoryname_);
	    gget(gfilename_, filename_, directoryname_);
	}
	else 
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	fstream::open(filename_.c_str(), mode);
	opened_=true;
	closed_=false;
    }
    return *this;
}


gstream& gstream::close()
{
    if ( closed_==false )
    {
	fstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
	closed_=true;
    }
    return *this;
}


igstream::igstream()
 : opened_(false), closed_(true)
{

}


igstream::igstream(const char* name, ios::openmode mode)
 : opened_(false), closed_(true)
{
    igstream::open(name, mode);
}


igstream::~igstream()
{
    igstream::close();
}


igstream& igstream::open(const char* name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    mkstagefile(gfilename_, filename_, directoryname_);
	    gget(gfilename_, filename_, directoryname_);
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	ifstream::open(filename_.c_str(), mode);
	opened_=true;
	closed_=false;
    }
    return *this;
}


igstream& igstream::close()
{
    if ( closed_==false )
    {
	ifstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
	closed_=true;
    }
    return *this;
}


ogstream::ogstream()
 : opened_(false), closed_(true)
{
}


ogstream::ogstream(const char* name, ios::openmode mode)
 : opened_(false), closed_(true)
{
    ogstream::open(name, mode);
}


ogstream::~ogstream()
{
    ogstream::close();
}


ogstream& ogstream::open(const char* name, ios::openmode mode)
{
    if ( opened_==false )
    {
	gfilename_=name;
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    mkstagefile(gfilename_, filename_, directoryname_);
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	ofstream::open(filename_.c_str(), mode);
	opened_=true;
	closed_=false;
    }
    return *this;
}


ogstream& ogstream::close()
{
    if ( closed_==false )
    {
	ofstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
	closed_=true;
    }
    return *this;
}



// ================= gpstream stuff ====================================


static void searchandreplace(string &str, const string &f, const string &r)
{
    unsigned int pos=str.find(f);
//    while ( pos!=str.npos ) { str.replace(pos, f.length(), r); pos=str.find(f); }
    while ( pos<str.length() ) { str.replace(pos, f.length(), r); pos=str.find(f); }
}


igpstream::igpstream()
 : opened_(false), closed_(true)
{
}


igpstream::igpstream(const string &name, const string &pipe)
 : opened_(false), closed_(true)
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
	    mkstagefile(gfilename_, filename_, directoryname_);
	    gget(gfilename_, filename_, directoryname_);
	}
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	searchandreplace(pipename_, "%f", filename_);
	ipstream::open(pipename_);
	opened_=true;
	closed_=false;
    }
    return *this;
}


igpstream& igpstream::close()
{
    if ( closed_==false )
    {
	ipstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" ) cleanup(filename_, directoryname_);
	opened_=false;
	closed_=true;
    }
    return *this;
}


ogpstream::ogpstream()
 : opened_(false), closed_(true)
{
}


ogpstream::ogpstream(const string &name, const string &pipe)
 : opened_(false), closed_(true)
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
	if ( gfilename_.substr(0, 6)=="/grid/" ) mkstagefile(gfilename_, filename_, directoryname_);
	else
	{
	    filename_=gfilename_;
	    directoryname_="";
	}
	searchandreplace(pipename_, "%f", filename_);
	opstream::open(pipename_);
	opened_=true;
	closed_=false;
    }
    return *this;
}


ogpstream& ogpstream::close()
{
    if ( closed_==false )
    {
	opstream::close();
	if ( gfilename_.substr(0, 6)=="/grid/" )
	{
	    gput(gfilename_, filename_, directoryname_);
	    cleanup(filename_, directoryname_);
	}
	opened_=false;
	closed_=true;
    }
    return *this;
}


}

