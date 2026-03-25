interface StarRatingProps {
  rating: number
  maxStars?: number
  size?: number
}

export default function StarRating({ rating, maxStars = 5, size = 14 }: StarRatingProps) {
  return (
    <span className="star-rating" aria-label={`${rating} out of ${maxStars} stars`}>
      {Array.from({ length: maxStars }, (_, i) => (
        <span
          key={i}
          className={`star${i < Math.round(rating) ? '' : ' empty'}`}
          style={{ fontSize: size }}
        >
          ★
        </span>
      ))}
    </span>
  )
}
