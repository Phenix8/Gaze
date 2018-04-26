#include <iostream>
#include <dlib/dnn.h>
#include <dlib/data_io.h>
#include <dlib/gui_widgets.h>
#include <dlib/dir_nav.h>
#include <dlib/cmd_line_parser.h>

#include "network_definition.h"

using namespace std;
using namespace dlib;

void testDetector(
	const string &networkFileName,
	std::vector<matrix<rgb_pixel>> &images,
	std::vector<std::vector<mmod_rect>> &boxes,
	unsigned int upsampling,
	bool silent
) {    
	test_net_type net;

	deserialize(networkFileName) >> net;

	if (boxes.size() != 0) {
		cout << "Results on dataset :  " << test_object_detection_function(net, images, boxes) << endl;
	}

	boxes.clear();

	image_window win;
	for (auto&& img : images)
	{
		for (unsigned int i=0; i<upsampling; i++) {
			pyramid_up(img);
		}

		std::vector<mmod_rect> dets = net(img);
		
		std::vector<mmod_rect> downsampledDets;
		for (const auto &d : dets) {
			mmod_rect newDet = d;
			
			for (int i=0; i<upsampling; i++) {
				newDet.rect.right() >>= 1;
				newDet.rect.left() >>= 1;
				newDet.rect.top() >>= 1;
				newDet.rect.bottom() >>= 1;
			}
			downsampledDets.push_back(newDet);
		} 
		
		boxes.push_back(downsampledDets);		

		if (!silent) {
			win.clear_overlay();
			win.set_image(img);
		
			for (auto&& d : dets) {
				win.add_overlay(d);
			}
			cin.get();
		}
	}	
}


void trainDetector(
	const string &datasetFileName,
	const string &backupFileName,
	const string &outputFileName,
	unsigned int windowSize,
	double minLearningRate,
	unsigned int iterationWProgress,
	unsigned int backupInterval
) {
    	std::vector<matrix<rgb_pixel>> images;
	std::vector<std::vector<mmod_rect>> boxes;

	cout << "Loading training dataset from metadata file" << datasetFileName << endl;
	load_image_dataset(images, boxes, datasetFileName);
	cout << "Training images count : " << images.size() << endl;

	unsigned int labels = 0;
	for (const auto &boxOnImage : boxes) {
		labels += boxOnImage.size();
	}

	cout << "Labelled samples count : " << labels << endl;

	mmod_options options(boxes, windowSize, windowSize);
	cout << "Detector windows count : "<< options.detector_windows.size() << endl;
	for (auto& w : options.detector_windows)
	cout << "\tDetector window width by height: " << w.width << " x " << w.height << endl;
	cout << "Overlap NMS IOU thresh:             " << options.overlaps_nms.get_iou_thresh() << endl;
	cout << "Overlap NMS percent covered thresh: " << options.overlaps_nms.get_percent_covered_thresh() << endl;

	net_type net(options);
	net.subnet().layer_details().set_num_filters(options.detector_windows.size());
	
	dnn_trainer<net_type> trainer(net);
	trainer.set_learning_rate(0.1);
	trainer.be_verbose();
	trainer.set_synchronization_file(backupFileName, std::chrono::minutes(backupInterval));
	trainer.set_iterations_without_progress_threshold(iterationWProgress);

	std::vector<matrix<rgb_pixel>> mini_batch_samples;
	std::vector<std::vector<mmod_rect>> mini_batch_labels; 
	random_cropper cropper;
	cropper.set_randomly_flip(true);
    	cropper.set_max_rotation_degrees(90);
	dlib::rand rnd;

	while(trainer.get_learning_rate() >= minLearningRate)
	{
		cropper(25, images, boxes, mini_batch_samples, mini_batch_labels);

		for (auto&& img : mini_batch_samples)
		    disturb_colors(img, rnd);

		trainer.train_one_step(mini_batch_samples, mini_batch_labels);
	}

	trainer.get_net();
	cout << "Done training" << endl;

	cout << "Cleaning network" << endl;
	net.clean();

	cout << "Saving network weights in output file " << outputFileName << endl;
	serialize(outputFileName) << net;

	cout << endl << "------ parameters ------" << endl;
	cout << trainer << cropper << endl;

	cout << endl << "Results on training dataset : " << test_object_detection_function(net, images, boxes) << endl;
}

bool fileExists(const string& name)
{
	ifstream f(name);
	return f.good();
}

bool fileIsWritable(const string &name) {
	ofstream f(name, fstream::app);
	return f.good();
}

string fileExtension(const string &fileName) {
	size_t n = fileName.find_last_of(".");
	if (n == string::npos) {
		return "";
	}

	return fileName.substr(n+1);
}

int main(int argc, char** argv) try
{
	command_line_parser parser;
	parser.add_option("h","Display this help message.", 0);
	parser.add_option("train","Train an object detector and save the detector to disk.", 0);
	parser.add_option("test","Test the specified detector on a given dataset.", 0);
	parser.add_option("silent", "Be silent", 0);

	parser.set_group_name("training sub-options");
	parser.add_option("w", "Specify the detection window size. Default = 80", 1);
	parser.add_option("r", "Specify the minimum learning rate. Default = 1e-4", 1);
	parser.add_option("i", "Specify the number of iterations without progress before decreasing learning rate. Default = 4000", 1);
	parser.add_option("b", "Specify the name of the progression backup file. Default = training_backup", 1);
	parser.add_option("t", "Specify the the progression backup interval in minutes. Default = 5", 1);
	parser.add_option("o", "Specify the name of output file. Default = network.dat", 1);

	parser.set_group_name("testing sub-options");
	parser.add_option("n", "Load the specified network to perform detections", 1);
	parser.add_option("s", "Save detections in the specified dataset file.", 1);
	parser.add_option("u", "Upsample images N time before performing detection. Default = 0", 1);

	parser.parse(argc, argv);

	const char* one_time_opts[] = {"h", "train", "test", "w", "r", "i",
                                        "t", "b", "o", "n", "s", "silent", "u"};

	parser.check_one_time_options(one_time_opts);

	const char* incompatible[] = {"train", "test"};
        parser.check_incompatible_options(incompatible);

	parser.check_option_arg_range("w", 10, 200);
	parser.check_option_arg_range("r", 1e-6, 1e-1);
	parser.check_option_arg_range("i", 10, 50000);
	parser.check_option_arg_range("t", 1, 20);
	parser.check_option_arg_range("u", 1, 6);
	
	const char* training_ops[] = {"train"};
        const char* training_sub_ops[] = {"w", "r", "i", "b", "t"};
	parser.check_sub_options(training_ops, training_sub_ops);

	const char* testing_ops[] = {"test"};
        const char* testing_sub_ops[] = {"n", "s", "u"};
	parser.check_sub_options(testing_ops, testing_sub_ops);

	if (parser.option("h"))
        {
		cout << "Usage: trainer++ --train [options] <image dataset file>\n";
		cout << "       trainer++ --test [options] <image dataset file|image file|image directory>\n";
		parser.print_options(); 
				       
		return EXIT_SUCCESS;
        }

	if (parser.option("train")) {
		if (parser.number_of_arguments() != 1) {
			cout << "You must give an image dataset file (dlib format)." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		const string &datasetFile = parser[0];
		const string &backupFile = get_option(parser, "b", "training_backup");
		const string &outputFile = get_option(parser, "o", "network.dat");

		if (!fileExists(datasetFile)) {
			cout << "Dataset file (" << datasetFile << ") cannot be opened, please verify parameters or specify another name." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		if (fileExists(backupFile)) {
			cout << "Backup file (" << backupFile << ") already exists and training state will be restored from it, continue ? (y to continue) : ";
			if (cin.get() != 'y') {
				return EXIT_FAILURE;
			}
			cin.clear();
			cin.ignore(INT_MAX, '\n');
		}

		if (fileExists(outputFile)) {
			cout << "Output file (" << outputFile << ") already exists and will overriden, continue (y to continue) : ";
			if (cin.get() != 'y') {
				return EXIT_FAILURE;
			}
			cin.clear();
			cin.ignore(INT_MAX, '\n');
		}


		if (!fileIsWritable(outputFile)) {
			cout << "Output file (" << outputFile << ") cannot be created or writed, please specify another name." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		unsigned int windowSize = get_option(parser, "w", 80);
		double minLearningRate = get_option(parser, "r", 1e-4);
		unsigned int iterationWProgress = get_option(parser, "i", 4000);
		unsigned int backupInterval = get_option(parser, "t", 5);

		try {
			trainDetector(
				datasetFile,
				backupFile,
				outputFile,
				windowSize,
				minLearningRate,
				iterationWProgress,
				backupInterval
			);
		} catch (const exception &e) {
			cout << "An error append during training : " << e.what() << endl;
		}		
	}

	if (parser.option("test")) {
		if (parser.number_of_arguments() != 1) {
			cout << "You must give an image dataset file (dlib format), a directory name, or an image name." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		const string &testFile = parser[0];
		const string &networkFile = get_option(parser, "n", "");

		if (networkFile.length() == 0) {
			cout << "You must use the -n option to give the file from wich the trained network will be loaded." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		if (!fileExists(networkFile)) {
			cout << "Network file (" << networkFile << ") cannot be opened, please verify parameters or specify another name." << endl;
			cout << "Try the -h option for more information." << endl;
			return EXIT_FAILURE;
		}

		string outputFile = get_option(parser, "s", "");
		bool silent = parser.option("silent");
		unsigned int upsampling = get_option(parser, "u", 0);

		std::vector<matrix<rgb_pixel>> images;
		std::vector<std::vector<mmod_rect>> boxes;
		std::vector<file> loadedFiles;

		try {
			directory dir(testFile);
			
			cout << "Loading directory " << testFile << "..." << endl;

			std::vector<file> files = dir.get_files();

			for (const auto &file : files) {
				try {
					matrix<rgb_pixel> image;
					load_image(image, file.full_name());
					
					images.push_back(image);
					loadedFiles.push_back(file);					
				} catch (const image_load_error &e) {
					cout << "Can't load file " << file.name() << " : " << e.what() << endl;
				}
			}
			
		} catch (const directory::dir_not_found &e) {
			
			if (!fileExists(testFile)) {
				cout << "Test file (" << testFile << ") cannot be opened, please verify parameters or specify another name." << endl;
				cout << "Try the -h option for more information." << endl;
				return EXIT_FAILURE;
			}

			string extension = fileExtension(testFile);

			if (testFile.length() == 0) {
				cout << "Cannot determine the type of file " << testFile << ", it has no extension." << endl;
				cout << "Try the -h option for more information." << endl;
				return EXIT_FAILURE;
			}
			
			if (extension.compare("xml") == 0) {
				cout << "Loading " << testFile << " as a dataset..." << endl;
				try {
					image_dataset_metadata::dataset dataset;
					image_dataset_metadata::load_image_dataset_metadata(dataset, testFile);
					
					locally_change_current_dir cd(get_parent_directory(file(testFile)));
					for (const auto &image : dataset.images) {
						loadedFiles.push_back(file(image.filename));
					}
					cd.revert();

					load_image_dataset(images, boxes, testFile);
				} catch (const dlib::error &e) {
					cout << "Error loading dataset : " << e.what() << endl;
					cout << "Try the -h option for more information." << endl;
					return EXIT_FAILURE;
				}
			} else {
				try {
					matrix<rgb_pixel> image;
					load_image(image, testFile);
					
					images.push_back(image);
					loadedFiles.push_back(testFile);					
				} catch (const image_load_error &e) {
					cout << "Can't load file " << testFile << " : " << e.what() << endl;
					cout << "Try the -h option for more information." << endl;
					return EXIT_FAILURE;
				}
			}
		}

		testDetector(networkFile, images, boxes, upsampling, silent);

		unsigned int imagesWithDetections = 0;
		unsigned int totalDetections = 0;

		for (const auto &dets : boxes) {
			totalDetections += dets.size();
			if (dets.size() > 0) {
				imagesWithDetections++;
			}
		}

		std::cout << "Images count : " << images.size() << endl;
		std::cout << "Images with detections count : " << imagesWithDetections << endl;
		std::cout << "Total of detections : " << totalDetections << endl;

		if (outputFile.length() != 0) {
			image_dataset_metadata::dataset dataset;

			for (int i=0; i<images.size(); i++) {
				image_dataset_metadata::image image(loadedFiles[i]);
				for (const auto &rect : boxes[i]) {
					image.boxes.push_back(image_dataset_metadata::box(rect));
				}
				dataset.images.push_back(image);
			}

			image_dataset_metadata::save_image_dataset_metadata(dataset, outputFile);

			cout << endl << "Saved detections in dataset metadat file " << outputFile << "." << endl;
		}
	}

    return 0;

}
catch(std::exception& e)
{
    cout << e.what() << endl;
}
