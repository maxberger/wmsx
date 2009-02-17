#ifndef GSTREAM_H
#define GSTREAM_H

/**
 * Uses 'pstream.h' from http://pstreams.sourceforge.net, which is 
 * distributed under the LGPL license, so you must respect it.
 * To use the LCG storage facility, first set the environmental variables:
 * export LCG_CATALOG_TYPE=lfc
 * export LFC_HOST=your_lfc_host
 * Then, to use this library, set the environmental variables:
 * export LCG_GFAL_VO=your_virtual_organisation
 * export DEST=your_storage_element
 * Don't forget to authenticate yourself with 'grid-proxy-init' or 
 * voms-proxy-init.
 * One can also set the environmental variable TMPDIR:
 * export TMPDIR=some_directory
 * This will be used as stage directory. If not set, $PWD is used.
 * 
 * @author Andras Laszlo
 */


#include <fstream>
#include <string>
#include <libgstream/pstream.h>


namespace std
{


/** Switch on/off gstream debug messages. */
void gstream_debug(const bool);
bool gstream_debug();


/** File stream (read-write) on the GRID. */
class gstream : public fstream
{
    protected:
	bool opened_;
	string gfilename_;
	string directoryname_;
	string filename_;

    public:
	gstream();
	gstream(const string&, ios::openmode mode=ios::in|ios::out);
	~gstream();
	gstream& open(const string&, ios::openmode mode=ios::in|ios::out);
	gstream& close();
};


/** Input file stream (read) on the GRID. */
class igstream : public ifstream
{
    protected:
	bool opened_;
	string gfilename_;
	string directoryname_;
	string filename_;
    public:
	igstream();
	igstream(const string&, ios::openmode mode=ios::in);
	~igstream();
	igstream& open(const string&, ios::openmode mode=ios::in);
	igstream& close();
};


/** Output file stream (write) on the GRID. */
class ogstream : public ofstream
{
    protected:
	bool opened_;
	string gfilename_;
	string directoryname_;
	string filename_;
    public:
	ogstream();
	ogstream(const string&, ios::openmode mode=ios::out);
	~ogstream();
	ogstream& open(const string&, ios::openmode mode=ios::out);
	ogstream& close();
};


/**
 * Input file stream (read) on the GRID, through a pipe (filter program).
 * '%f' in the pipe command is replace by the file name.
 */
class igpstream : public std::ipstream
{
    protected:
	bool opened_;
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


/**
 * Output file stream (write) on the GRID, through a pipe (filter program).
 * '%f' in the pipe command is replace by the file name.
 */
class ogpstream : public std::opstream
{
    protected:
	bool opened_;
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


} // namespace std


#endif /* GSTREAM_H */
