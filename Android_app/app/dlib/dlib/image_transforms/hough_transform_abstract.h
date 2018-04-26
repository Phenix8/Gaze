// Copyright (C) 2014  Davis E. King (davis@dlib.net)
// License: Boost Software License   See LICENSE.txt for the full license.
#undef DLIB_HOUGH_tRANSFORM_ABSTRACT_Hh_
#ifdef DLIB_HOUGH_tRANSFORM_ABSTRACT_Hh_

#include "../geometry.h"
#include "../image_processing/generic_image.h"

namespace dlib
{

// ----------------------------------------------------------------------------------------

    class hough_transform
    {
        /*!
            WHAT THIS OBJECT REPRESENTS
                This object is a tool for computing the line finding version of the Hough
                transform given some kind of edge detection image as input.  It also allows
                the edge pixels to be weighted such that higher weighted edge pixels
                contribute correspondingly more to the output of the Hough transform,
                allowing stronger edges to create correspondingly stronger line detections
                in the final Hough transform.

            THREAD SAFETY
                It is safe for multiple threads to make concurrent accesses to this object
                without synchronization.
        !*/

    public:

        explicit hough_transform (
            unsigned long size_
        ); 
        /*!
            requires
                - size_ > 0
            ensures
                - This object will compute Hough transforms that are size_ by size_ pixels.  
                  This is in terms of both the Hough accumulator array size as well as the
                  input image size.
                - #size() == size_
        !*/

        unsigned long size(
        ) const;
        /*!
            ensures
                - returns the size of the Hough transforms generated by this object.  In
                  particular, this object creates Hough transform images that are size() by
                  size() pixels in size.
        !*/

        long nr(
        ) const;
        /*!
            ensures
                - returns size()
        !*/

        long nc(
        ) const;
        /*!
            ensures
                - returns size()
        !*/

        std::pair<point, point> get_line (
            const point& p
        ) const;
        /*!
            requires
                - rectangle(0,0,size()-1,size()-1).contains(p) == true
                  (i.e. p must be a point inside the Hough accumulator array)
            ensures
                - returns the line segment in the original image space corresponding
                  to Hough transform point p. 
                - The returned points are inside rectangle(0,0,size()-1,size()-1).
        !*/

        template <
            typename image_type
            >
        point get_best_hough_point (
            const point& p,
            const image_type& himg
        );
        /*!
            requires
                - image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - himg.nr() == size()
                - himg.nc() == size()
                - rectangle(0,0,size()-1,size()-1).contains(p) == true
            ensures
                - This function interprets himg as a Hough image and p as a point in the
                  original image space.  Given this, it finds the maximum scoring line that
                  passes though p.  That is, it checks all the Hough accumulator bins in
                  himg corresponding to lines though p and returns the location with the
                  largest score.  
                - returns a point X such that get_rect(himg).contains(X) == true
        !*/

        template <
            typename in_image_type,
            typename out_image_type
            >
        void operator() (
            const in_image_type& img,
            const rectangle& box,
            out_image_type& himg
        ) const;
        /*!
            requires
                - in_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - out_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - box.width() == size()
                - box.height() == size()
            ensures
                - Computes the Hough transform of the part of img contained within box.
                  In particular, we do a grayscale version of the Hough transform where any
                  non-zero pixel in img is treated as a potential component of a line and
                  accumulated into the Hough accumulator #himg.  However, rather than
                  adding 1 to each relevant accumulator bin we add the value of the pixel
                  in img to each Hough accumulator bin.  This means that, if all the
                  pixels in img are 0 or 1 then this routine performs a normal Hough
                  transform.  However, if some pixels have larger values then they will be
                  weighted correspondingly more in the resulting Hough transform.
                - #himg.nr() == size()
                - #himg.nc() == size()
                - #himg is the Hough transform of the part of img contained in box.  Each
                  point in #himg corresponds to a line in the input box.  In particular,
                  the line for #himg[y][x] is given by get_line(point(x,y)).  Also, when
                  viewing the #himg image, the x-axis gives the angle of the line and the
                  y-axis the distance of the line from the center of the box.
        !*/

        template <
            typename in_image_type,
            typename out_image_type
            >
        void operator() (
            const in_image_type& img,
            out_image_type& himg
        ) const;
        /*!
            requires
                - in_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - out_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - num_rows(img) == size()
                - num_columns(img) == size()
            ensures
                - performs: (*this)(img, get_rect(img), himg);
                  That is, just runs the hough transform on the whole input image.
        !*/

        template <
            typename in_image_type
            >
        std::vector<std::vector<point>> find_pixels_voting_for_lines (
            const in_image_type& img,
            const rectangle& box,
            const std::vector<point>& hough_points
        ) const;
        /*!
            requires
                - in_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - box.width() == size()
                - box.height() == size()
                - for all valid i:
                    - get_rect(*this).contains(hough_points[i]) == true
                      (i.e. hough_points must contain points in the output Hough transform
                      space generated by this object.)
            ensures
                - This function computes the Hough transform of the part of img contained
                  within box.  It does the same computation as operator() define above,
                  except instead of accumulating into an image we create an explicit list
                  of all the points in img that contributed to each line (i.e each point in
                  the Hough image). To do this we take a list of Hough points as input and
                  only record hits on these specifically identified Hough points.  A
                  typical use of find_pixels_voting_for_lines() is to first run the normal
                  Hough transform using operator(), then find the lines you are interested
                  in, and then call find_pixels_voting_for_lines() to determine which
                  pixels in the input image belong to those lines.
                - This routine returns a vector, call the returned vector CONSTITUENT_POINTS.
                  It has the following properties:
                - #CONSTITUENT_POINTS.size() == hough_points.size()
                - for all valid i:
                    - Any point in img with a non-zero value that lies on  the line
                      corresponding to the Hough point hough_points[i] is added to
                      CONSTITUENT_POINTS[i].  Therefore, when this routine finishes,
                      #CONSTITUENT_POINTS[i] will contain all the points in img that voted
                      for the line hough_points[i].
                    - #CONSTITUENT_POINTS[i].size() == the number of points in img that voted for
                      the line hough_points[i]. 
        !*/

        template <
            typename in_image_type
            >
        std::vector<std::vector<point>> find_pixels_voting_for_lines (
            const in_image_type& img,
            const std::vector<point>& hough_points
        ) const;
        /*!
            requires
                - in_image_type == an image object that implements the interface defined in
                  dlib/image_processing/generic_image.h and it must contain grayscale pixels.
                - num_rows(img) == size()
                - num_columns(img) == size()
                - for all valid i:
                    - get_rect(*this).contains(hough_points[i]) == true
                      (i.e. hough_points must contain points in the output Hough transform
                      space generated by this object.)
            ensures
                - performs: return find_pixels_voting_for_lines(img, get_rect(img), hough_points);
                  That is, just runs the hough transform on the whole input image.
        !*/

    };
}

#endif // DLIB_HOUGH_tRANSFORM_ABSTRACT_Hh_


