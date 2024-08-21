import React, { useState } from 'react'
import { Wrapper } from './ProductImages.styles';

const ProductImages: React.FC<{ images: string[] | undefined }> = ({
  images = [],
}) => {
  const [imageIndex, setImageIndex] = useState(0)

  return (
    <Wrapper>
      <img src={images[imageIndex]} alt='main' className='main' />
      <div className='gallery'>
        {images.map((image, index) => {
          return (
            <img
              key={image}
              src={image}
              alt=''
              onClick={() => setImageIndex(index)}
              className={index===imageIndex? 'active': undefined}
            />
          )
        })}
      </div>
    </Wrapper>
  )
}

export default ProductImages
