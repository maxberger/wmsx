#ifndef __GSTREAM_H
#define __GSTREAM_H

// Uses 'pstream.h' from http://pstreams.sourceforge.net, which is 
// distributed under the LGPL license, so you must respect it.
// To use the LCG storage facility, first set the environmental variables:
// export LCG_CATALOG_TYPE=lfc
// export LFC_HOST=lfc-cms-test.cern.ch
// Then, to use this library, set the environmental variables:
// export VO=your_virtual_organisation
// export DEST=your_storage_element
// Don't forget to authenticate yourself with 'grid-proxy-init'.
// One can also set the environmental variable TMPDIR:
// export TMPDIR=some_directory
// This will be used as stage directory. If not set, PWD is used.


#include <cstring>
#include <cstdlib>
#include <unistd.h>
#include <signal.h>
#include <sys/wait.h>
#include <errno.h>
#include <streambuf>
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <gstream/pstream.h>


namespace std
{


// Erase stage file and directory.
extern void cleanup(const string &filename, const string &directoryname);


// Prepare stage file and directory.
extern void mkstagefile(const string &gfilename, string &filename, string &directoryname);


// Get file.
extern void gget(const string &gfilename, const string &filename, const string &directoryname);


// Put file.
extern void gput(const string &gfilename, const string &filename, const string &directoryname);


// File stream (read-write) on the GRID.
class gstream : public fstream
{
    protected:
	bool opened_;
	bool closed_;
	string gfilename_;
	string directoryname_;
	string filename_;
    public:
	gstream();
	gstream(const char*, ios::openmode mode=ios::in|ios::out);
	~gstream();
	gstream& open(const char*, ios::openmode mode=ios::in|ios::out);
	gstream& close();
};


// Input file stream (read) on the GRID.
class igstream : public ifstream
{
    protected:
	bool opened_;
	bool closed_;
	string gfilename_;
	string directoryname_;
	string filename_;
    public:
	igstream();
	igstream(const char*, ios::openmode mode=ios::in);
	~igstream();
	igstream& open(const char*, ios::openmode mode=ios::in);
	igstream& close();
};


// Output file stream (write) on the GRID.
class ogstream : public ofstream
{
    protected:
	bool opened_;
	bool closed_;
	string gfilename_;
	string directoryname_;
	string filename_;
    public:
	ogstream();
	ogstream(const char*, ios::openmode mode=ios::out);
	~ogstream();
	ogstream& open(const char*, ios::openmode mode=ios::out);
	ogstream& close();
};


// Input file stream (read) on the GRID, through a pipe (filter program).
// '%f' in the pipe command is replace by the file name.
class igpstream : public std::ipstream
{
    protected:
	bool opened_;
	bool closed_;
	string gfilename_;
	string directoryname_;
	string filename_;
	string pipename_;
    public:
	igpstream();
	igpstream(const string &gfilename, const string &pipename="cat %f");
	~igpstream();
	igpstream& open(const string &gfilename, const string &pipename);
	igpstream& close();
};


// Output file stream (write) on the GRID, through a pipe (filter program).
// '%f' in the pipe command is replace by the file name.
class ogpstream : public std::opstream
{
    protected:
	bool opened_;
	bool closed_;
	string gfilename_;
	string directoryname_;
	string filename_;
	string pipename_;
    public:
	ogpstream();
	ogpstream(const string &gfilename, const string &pipename="cat - > %f");
	~ogpstream();
	ogpstream& open(const string &gfilename, const string &pipename);
	ogpstream& close();
};


}


#endif
